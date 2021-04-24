package com.splitthebill.server.controller;

import com.splitthebill.server.model.user.Notification;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.model.user.UserAccountNotification;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.NotificationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs(value = "target/generated-snippets/notification",
        uriScheme = "https",
        uriHost = "softstone.pl",
        uriPort = 8443)
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "splitthebill.app.scheduling-enabled=false")
public class NotificationControllerTest {

    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private NotificationService notificationService;

    @Autowired
    private MockMvc mockMvc;


    @WithMockUser
    @Test
    public void testGetAllNotifications() throws Exception {
        UserAccount mockUserAccount = mock(UserAccount.class);
        Notification pendingDebtNotification = new Notification("Pending debt",
                "You have a pending debt to Person Two!");
        Notification systemUpdate = new Notification("System update",
                "There will be system maintenance starting on 01.01.2030 2AM " +
                        "and expected to be finished by 4AM. " +
                        "The application will be unreachable during the maintenance. Sorry for the inconvenience!");
        UserAccountNotification mockAccountDebtNotification =
                new UserAccountNotification(pendingDebtNotification, mockUserAccount);
        UserAccountNotification mockAccountSystemUpdateNotification =
                new UserAccountNotification(systemUpdate, mockUserAccount);
        when(jwtUtils.getUserAccountFromAuthentication(any(Authentication.class))).thenReturn(mockUserAccount);
        when(notificationService.getUserAccountNotifications(mockUserAccount))
                .thenAnswer(p ->
                        {
                            mockAccountDebtNotification.setId(1L);
                            mockAccountSystemUpdateNotification.setId(2L);
                            return List.of(mockAccountDebtNotification, mockAccountSystemUpdateNotification);
                        }
                );

        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andDo(document("get-all-notifications",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id")
                                        .description("Id of a user notification."),
                                fieldWithPath("[].title")
                                        .description("Title of the notification."),
                                fieldWithPath("[].description")
                                        .description("The description of the notification."),
                                fieldWithPath("[].isReviewed")
                                        .description("A flag stating whether the user has already read the notification.")
                        )));
    }

    @WithMockUser
    @Test
    public void testMarkNotificationReviewed() throws Exception {
        UserAccount userAccount = mock(UserAccount.class);
        Notification pendingDebtNotification = new Notification("Pending debt",
                "You have a pending debt to Person Two!");
        UserAccountNotification mockAccountDebtNotification =
                new UserAccountNotification(pendingDebtNotification, userAccount);
        mockAccountDebtNotification.setId(1L);
        when(jwtUtils.getUserAccountFromAuthentication(any(Authentication.class))).thenReturn(userAccount);
        when(notificationService.getUserAccountNotificationById(1L)).thenReturn(mockAccountDebtNotification);
        when(notificationService.markUserAccountNotificationReviewed(mockAccountDebtNotification)).thenAnswer(
                p -> {
                    mockAccountDebtNotification.markReviewed();
                    return mockAccountDebtNotification;
                }
        );

        mockMvc.perform(patch("/notifications/{id}/markReviewed", 1))
                .andExpect(status().isOk())
                .andDo(document("mark-notification-reviewed",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id")
                                        .description("Id of a notification to be marked reviewed.")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .description("Id of a user notification."),
                                fieldWithPath("title")
                                        .description("Title of the notification."),
                                fieldWithPath("description")
                                        .description("The description of the notification."),
                                fieldWithPath("isReviewed")
                                        .description("A flag stating whether the user has already read the notification.")
                        )));
    }

}
