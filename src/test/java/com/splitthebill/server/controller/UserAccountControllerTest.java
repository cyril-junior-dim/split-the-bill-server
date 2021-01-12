package com.splitthebill.server.controller;

import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.service.UserAccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs("target/generated-snippets/user-account")
@WebMvcTest(controllers = UserAccountController.class)
@RunWith(SpringRunner.class)
public class UserAccountControllerTest {

    @MockBean
    private UserAccountService userAccountService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetUserAccount() throws Exception {
        UserAccount userAccount = UserAccount.builder()
                .username("damien")
                .email("garbalad@gmail.com")
                .build();
        when(userAccountService.getUserAccountById(1L))
                .thenReturn(userAccount);
        mockMvc.perform(get("/userAccounts/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(document("get-user-account-by-id",
                        preprocessResponse(prettyPrint())
                ));
    }

}
