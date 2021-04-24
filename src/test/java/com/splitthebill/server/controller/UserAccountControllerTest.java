package com.splitthebill.server.controller;

import com.splitthebill.server.model.user.BasicUserAccount;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.BasicUserAccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs(value = "target/generated-snippets/user-account",
        uriScheme = "https",
        uriHost = "softstone.pl",
        uriPort = 8443)
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "splitthebill.app.scheduling-enabled=false")
public class UserAccountControllerTest {

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser
    @Test
    public void testGetUserAccount() throws Exception {
        BasicUserAccount userAccount = BasicUserAccount.builder()
                .username("username")
                .email("test@test.com")
                .build();
        when(jwtUtils.getUserAccountFromAuthentication(any(Authentication.class))).thenReturn(userAccount);
        mockMvc.perform(get("/userAccounts"))
                .andExpect(status().isOk())
                .andDo(document("get-user-account",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("email")
                                        .description("Email assigned to user account."),
                                subsectionWithPath("_links").ignored()
                        ),
                        links(
                                linkWithRel("self")
                                        .description("Self reference"),
                                linkWithRel("person")
                                        .description("Link to person assigned to user account."),
                                linkWithRel("notifications")
                                        .description("Link to notifications assigned to user account.")
                        )
                ));
    }

}
