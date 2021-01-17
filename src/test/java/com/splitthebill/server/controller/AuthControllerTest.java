package com.splitthebill.server.controller;

import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.BasicUserAccountService;
import com.splitthebill.server.service.ThirdPartyUserAccountService;
import lombok.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@AutoConfigureRestDocs("target/generated-snippets/auth")
@WebMvcTest(controllers = AuthController.class)
@RunWith(SpringRunner.class)
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

    @Test
    public void testAuthenticateUser(){
        String postBody = "{" +
                "\"username\": \"sampleUser12\"," +
                "\"password\": \"secretPassword\"" +
                "}";
    }
}
