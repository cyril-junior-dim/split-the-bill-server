package com.splitthebill.server.controller;

import com.splitthebill.server.dto.PersonCreateDto;
import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.PersonService;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs(value = "target/generated-snippets/person",
        uriScheme = "https",
        uriHost = "softstone.pl",
        uriPort = 8443)
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
public class PersonControllerTest {

    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private PersonService personService;

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser
    @Test
    public void testGetPerson() throws Exception {
        Currency eur = new Currency("EUR", new BigDecimal("1"));
        Person person = Person.builder()
                .id(1L)
                .name("Test Person")
                .userAccount(mock(UserAccount.class))
                .balances(Map.of(eur, new BigDecimal("10.75")))
                .preferredCurrency(new Currency("USD", BigDecimal.ONE))
                .personGroups(new ArrayList<>())
                .build();
        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(person);
        mockMvc.perform(get("/people"))
                .andExpect(status().isOk())
                .andDo(document("get-person",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("id")
                                        .description("Id of a person."),
                                fieldWithPath("name")
                                        .description("Name of a person."),
                                subsectionWithPath("balances")
                                        .description("Person's balances in different currencies."),
                                subsectionWithPath("_links")
                                        .description("Links to resources.")
                        ),
                        links(
                                linkWithRel("self").description("Self link"),
                                linkWithRel("userAccount").description("Link to associated user account."),
                                linkWithRel("friendships").description("Link to user friendships.")
                        )));
    }

    @WithMockUser
    @Test
    public void testCreatePerson() throws Exception {
        String postBody = "{" +
                "\"name\": \"Test Person\"," +
                "\"currencyAbbreviation\": \"EUR\"" +
                "}";
        Person expectedPerson = Person.builder()
                .name("Test Person")
                .userAccount(mock(UserAccount.class))
                .preferredCurrency(new Currency("USD", BigDecimal.ONE))
                .personGroups(new ArrayList<>())
                .balances(Map.of(new Currency("EUR", new BigDecimal("1")), BigDecimal.ZERO))
                .build();
        when(jwtUtils.getUserAccountFromAuthentication(any(Authentication.class))).thenReturn(mock(UserAccount.class));
        when(personService.createPerson(any(UserAccount.class), any(PersonCreateDto.class)))
                .thenAnswer(p -> {
                        expectedPerson.setId(1L);
                        return expectedPerson;
                    }
                );
        mockMvc.perform(post("/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postBody))
                .andExpect(status().isCreated())
                .andDo(document("create-person",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name")
                                        .description("A name to be given to person."),
                                fieldWithPath("currencyAbbreviation")
                                        .description("Abbreviation of the currency preferred by the user.")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .description("Id of a person."),
                                fieldWithPath("name")
                                        .description("Name of a person."),
                                subsectionWithPath("balances")
                                        .description("Person's balances in different currencies."),
                                subsectionWithPath("_links")
                                        .description("Links to resources.")
                        ),
                        links(
                                linkWithRel("self").description("Self link"),
                                linkWithRel("userAccount").description("Link to associated user account."),
                                linkWithRel("friendships").description("Link to user friendships.")
                        )));
    }

    @WithMockUser
    @Test
    public void testUpdatePerson() throws Exception {
        String patchBody = "{" +
                "\"name\": \"Updated Person\"," +
                "\"currencyAbbreviation\": \"USD\"" +
                "}";
        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(mock(Person.class));
        when(personService.updatePerson(any(Person.class), any(PersonCreateDto.class)))
                .thenAnswer(p -> Person.builder()
                        .id(1L)
                        .name("Updated Person")
                        .preferredCurrency(new Currency("USD", BigDecimal.ONE))
                        .personGroups(new ArrayList<>())
                        .userAccount(mock(UserAccount.class))
                        .balances(Map.of(new Currency("USD", new BigDecimal("0.85")), BigDecimal.ZERO))
                        .build()
                );
        mockMvc.perform(patch("/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody))
                .andExpect(status().isOk())
                .andDo(document("update-person",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name")
                                        .description("A name to be given to person."),
                                fieldWithPath("currencyAbbreviation")
                                        .description("Abbreviation of the currency preferred by the user.")
                        ),
                        responseFields(
                                fieldWithPath("id")
                                        .description("Id of a person."),
                                fieldWithPath("name")
                                        .description("Name of a person."),
                                subsectionWithPath("balances")
                                        .description("Person's balances in different currencies."),
                                subsectionWithPath("_links")
                                        .description("Links to resources.")
                        ),
                        links(
                                linkWithRel("self").description("Self link"),
                                linkWithRel("userAccount").description("Link to associated user account."),
                                linkWithRel("friendships").description("Link to user friendships.")
                        )));
    }
}
