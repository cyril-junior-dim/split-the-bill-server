package com.splitthebill.server.controller;

import com.splitthebill.server.dto.BasicUserAccountCreateDto;
import com.splitthebill.server.model.user.BasicUserAccount;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.BasicUserAccountService;
import com.splitthebill.server.service.ThirdPartyUserAccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs(value = "target/generated-snippets/auth",
        uriScheme = "https",
        uriHost = "softstone.pl",
        uriPort = 8443)
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.scheduling.enable=false")
public class AuthControllerTest {

    @MockBean
    AuthenticationManager authenticationManager;
    @MockBean
    BasicUserAccountService basicUserAccountService;
    @MockBean
    ThirdPartyUserAccountService thirdPartyUserAccountService;
    @MockBean
    PasswordEncoder encoder;
    @MockBean
    JwtUtils jwtUtils;

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser
    public void testAuthenticateUser() throws Exception {
        String postDto = "{" +
                "\"username\": \"username\"," +
                "\"password\": \"password\"" +
                "}";

        SecurityContextHolder.setContext(mock(SecurityContext.class));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(mock(Authentication.class));
        doNothing().when(SecurityContextHolder.getContext()).setAuthentication(any(Authentication.class));
        when(jwtUtils.generateJwtTokenForBasicAccount(any(Authentication.class))).thenReturn("tokenForUserAccount");
        mockMvc.perform(post("/auth/signin")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postDto))
                .andExpect(status().isOk())
                .andDo(document("authenticate-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("username").description("Username of existing account."),
                                fieldWithPath("password").description("Password assigned to existing account.")
                        ),
                        responseFields(
                                fieldWithPath("type").description("Type of a token."),
                                fieldWithPath("token").description("User token required for further API requests.")
                        )));
    }

    @Test
    @WithMockUser
    public void testRegisterUser() throws Exception {
        String postDto = "{" +
                "\"username\": \"username\"," +
                "\"password\": \"password\"," +
                "\"email\": \"test@test.com\"" +
                "}";

        when(encoder.encode("password")).thenReturn("encodedPassword");
        when(basicUserAccountService.createUserAccount(any(BasicUserAccountCreateDto.class)))
                .thenReturn(new BasicUserAccount());
        SecurityContextHolder.setContext(mock(SecurityContext.class));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(mock(Authentication.class));
        doNothing().when(SecurityContextHolder.getContext()).setAuthentication(any(Authentication.class));
        when(jwtUtils.generateJwtTokenForBasicAccount(any(Authentication.class))).thenReturn("tokenForUserAccount");
        mockMvc.perform(post("/auth/signup")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postDto))
                .andExpect(status().isOk())
                .andDo(document("register-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("username").description("Username for the account to be created."),
                                fieldWithPath("password").description("Password for the account to be created."),
                                fieldWithPath("email").description("Email for the account to be created.")
                        ),
                        responseFields(
                                fieldWithPath("type").description("Type of a token."),
                                fieldWithPath("token").description("User token required for further API requests.")
                        )));
    }

    @Test
    public void testSignInUsingGoogle() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection())
                .andDo(document("google-sign-in"));
    }

    @Test
    public void testSignInUsingFacebook() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/facebook"))
                .andExpect(status().is3xxRedirection())
                .andDo(document("facebook-sign-in"));
    }
}
