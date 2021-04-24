package com.splitthebill.server.controller;


import com.splitthebill.server.model.user.Friendship;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.FriendshipService;
import com.splitthebill.server.service.UserAccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs(value = "target/generated-snippets/friendship",
        uriScheme = "https",
        uriHost = "softstone.pl",
        uriPort = 8443)
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "splitthebill.app.scheduling-enabled=false")
public class FriendshipControllerTest {

    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private FriendshipService friendshipService;
    @MockBean
    private UserAccountService userAccountService;

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser
    @Test
    public void testGetAllFriendships() throws Exception {
        Person person1 = Person.builder()
                .name("Person One")
                .userAccount(mock(UserAccount.class))
                .build();
        Person person2 = Person.builder()
                .name("Person Two")
                .userAccount(mock(UserAccount.class))
                .build();
        Person person3 = Person.builder()
                .name("Person Three")
                .userAccount(mock(UserAccount.class))
                .build();
        Person person4 = Person.builder()
                .name("Person Four")
                .userAccount(mock(UserAccount.class))
                .build();
        Friendship sampleConfirmedFriendship = Friendship.builder()
                .id(1L)
                .person1(person1)
                .person2(person2)
                .confirmed(true)
                .build();
        Friendship samplePendingFriendship = Friendship.builder()
                .id(3L)
                .person1(person1)
                .person2(person3)
                .confirmed(false)
                .build();
        Friendship sampleFriendshipRequest = Friendship.builder()
                .id(4L)
                .person1(person1)
                .person2(person4)
                .confirmed(false)
                .build();
        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(mock(Person.class));
        when(friendshipService.getAllConfirmedFriendshipsForPerson(any(Person.class)))
                .thenReturn(List.of(sampleConfirmedFriendship));
        when(friendshipService.getAllPendingFriendshipForPerson(any(Person.class)))
                .thenReturn(List.of(samplePendingFriendship));
        when(friendshipService.getAllFriendshipRequestsForPerson(any(Person.class)))
                .thenReturn(List.of(sampleFriendshipRequest));

        mockMvc.perform(get("/friendships"))
                .andExpect(status().isOk())
                .andDo(document("get-all-friendships",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                beneathPath("friendships"),
                                subsectionWithPath("confirmed")
                                        .description("List of confirmed friendships"),
                                subsectionWithPath("pending")
                                        .description("List of friendships that has been issued " +
                                                "but not yet confirmed by the other person."),
                                subsectionWithPath("receivedRequests")
                                        .description("List of received friendship requests.")
                        )));
    }

    @WithMockUser
    @Test
    public void testSendFriendshipRequest() throws Exception {
        String postBody = "{" +
                "\"identifierAttribute\": \"emailOrUsername\"" +
                "}";
        Person sender = Person.builder()
                .name("Person One")
                .userAccount(mock(UserAccount.class))
                .build();
        Person receiver = Person.builder()
                .name("Person Two")
                .userAccount(mock(UserAccount.class))
                .build();

        UserAccount mockUserAccount = mock(UserAccount.class);
        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(sender);
        when(userAccountService.getUserAccountByIdentifierAttribute("emailOrUsername")).thenReturn(mockUserAccount);
        when(mockUserAccount.getPerson()).thenReturn(receiver);
        when(friendshipService.sendFriendshipRequest(sender, receiver)).thenAnswer(
                p -> Friendship.builder()
                        .id(1L)
                        .person1(sender)
                        .person2(receiver)
                        .confirmed(false)
                        .build()
        );

        mockMvc.perform(post("/friendships")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postBody))
                .andExpect(status().isOk())
                .andDo(document("send-friendship-request",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("identifierAttribute")
                                        .description("Username or email used to identify user account.")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .description("Id of a friendship."),
                                fieldWithPath("personName")
                                        .description("Name of a friend.")
                        )));
    }

    @WithMockUser
    @Test
    public void testAcceptFriendshipRequest() throws Exception {
        Person sender = Person.builder()
                .id(1L)
                .name("Person One")
                .userAccount(mock(UserAccount.class))
                .build();
        Person receiver = Person.builder()
                .id(2L)
                .name("Person Two")
                .userAccount(mock(UserAccount.class))
                .build();

        // This is reverse friendship, assuming that sender started the friendship
        Friendship friendshipToAccept = Friendship.builder()
                .id(2L)
                .person1(receiver)
                .person2(sender)
                .confirmed(false)
                .build();

        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(receiver);
        when(friendshipService.getFriendshipById(2L)).thenReturn(friendshipToAccept);
        when(friendshipService.acceptFriendshipRequest(2L)).thenAnswer(
                p -> {
                    friendshipToAccept.setConfirmed(true);
                    return friendshipToAccept;
                }
        );

        mockMvc.perform(patch("/friendships/{id}/accept", 2))
                .andExpect(status().isOk())
                .andDo(document("accept-friendship-request",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id")
                                        .description("Id of a friendship to be confirmed.")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .description("Id of a friendship."),
                                fieldWithPath("personName")
                                        .description("Name of a friend.")
                        )));
    }

    @WithMockUser
    @Test
    public void testBreakFriendship() throws Exception {
        Person sender = Person.builder()
                .id(1L)
                .name("Person One")
                .userAccount(mock(UserAccount.class))
                .build();
        Person receiver = Person.builder()
                .id(2L)
                .name("Person Two")
                .userAccount(mock(UserAccount.class))
                .build();

        Friendship friendshipToBreak = Friendship.builder()
                .id(1L)
                .person1(sender)
                .person2(receiver)
                .confirmed(true)
                .build();

        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(sender);
        when(friendshipService.getFriendshipById(1L)).thenReturn(friendshipToBreak);
        doNothing().when(friendshipService).breakFriendship(1L);

        mockMvc.perform(delete("/friendships/{id}/break", 1))
                .andExpect(status().isNoContent())
                .andDo(document("break-friendship-request",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id")
                                        .description("Id of a friendship to be broken.")
                        )));
    }
}
