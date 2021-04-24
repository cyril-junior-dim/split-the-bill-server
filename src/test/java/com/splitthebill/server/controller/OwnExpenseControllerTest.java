package com.splitthebill.server.controller;


import com.splitthebill.server.dto.expense.OwnExpenseCreateDto;
import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.expense.OwnExpense;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.OwnExpenseService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs(value = "target/generated-snippets/own-expense",
        uriScheme = "https",
        uriHost = "softstone.pl",
        uriPort = 8443)
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "splitthebill.app.scheduling-enabled=false")
public class OwnExpenseControllerTest {

    @MockBean
    private OwnExpenseService ownExpenseService;
    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;


    @WithMockUser
    @Test
    public void testGetAllExpenses() throws Exception {
        Currency euro = Currency.builder()
                .id(1L)
                .abbreviation("EUR")
                .exchangeRate(new BigDecimal("1"))
                .build();
        Person john = Person.builder()
                .id(1L)
                .name("John Doe")
                .balances(Map.of(euro, BigDecimal.ZERO))
                .preferredCurrency(euro)
                .build();
        OwnExpense cinema = OwnExpense.builder()
                .id(1L)
                .owner(john)
                .build();
        cinema.setAmount(new BigDecimal("10"));
        cinema.setCurrency(euro);
        cinema.setTitle("Cinema");
        cinema.setCreated(LocalDateTime.now());

        OwnExpense giftForMike = OwnExpense.builder()
                .id(2L)
                .owner(john)
                .build();
        giftForMike.setAmount(new BigDecimal("20"));
        giftForMike.setCurrency(euro);
        giftForMike.setTitle("Gift for Mike");
        giftForMike.setCreated(LocalDateTime.now());
        john.setOwnExpenses(List.of(cinema, giftForMike));

        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(john);
        mockMvc.perform(get("/expenses"))
                .andExpect(status().isOk())
                .andDo(document("get-all-expenses",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].ownExpenseId").description("Id of own expense"),
                                fieldWithPath("[].title").description("Title of own expense"),
                                fieldWithPath("[].amount").description("Amount of money spent"),
                                fieldWithPath("[].created").description("Date of own expense creation"),
                                fieldWithPath("[].receiptPhoto").description("Path to the receipt photo"),
                                fieldWithPath("[].currencyAbbreviation").description("The currency used for expense")
                        )));
    }


    @WithMockUser
    @Test
    public void testAddOwnExpense() throws Exception {
        Currency euro = Currency.builder()
                .id(1L)
                .abbreviation("EUR")
                .exchangeRate(new BigDecimal("1"))
                .build();
        Person john = Person.builder()
                .id(1L)
                .name("John Doe")
                .balances(Map.of(euro, BigDecimal.ZERO))
                .preferredCurrency(euro)
                .build();

        String postBody = "{" +
                "\"title\": \"Cinema\"," +
                "\"amount\": 10.20," +
                "\"currencyAbbreviation\": \"EUR\"," +
                "\"receiptPhoto\": \"\"" +
                "}";
        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(john);
        when(ownExpenseService.addExpense(any(OwnExpenseCreateDto.class), any(Person.class))).thenAnswer(
                p -> {
                    OwnExpenseCreateDto expenseDto = p.getArgument(0, OwnExpenseCreateDto.class);
                    OwnExpense ownExpense = new OwnExpense();
                    ownExpense.setId(1L);
                    ownExpense.setCurrency(euro);
                    ownExpense.setOwner(john);
                    ownExpense.setAmount(expenseDto.getAmount());
                    ownExpense.setTitle(expenseDto.getTitle());
                    ownExpense.setReceiptPhoto(expenseDto.getReceiptPhoto());
                    ownExpense.setCreated(LocalDateTime.now());
                    john.setOwnExpenses(List.of(ownExpense));
                    return ownExpense;
                }
        );
        mockMvc.perform(post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postBody))
                .andExpect(status().isOk())
                .andDo(document("create-own-expense",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").description("Title of an expense"),
                                fieldWithPath("amount").description("Amount spent"),
                                fieldWithPath("currencyAbbreviation").description("Currency used"),
                                fieldWithPath("receiptPhoto").ignored()
                        ),
                        responseFields(
                                fieldWithPath("ownExpenseId").description("Id of own expense"),
                                fieldWithPath("title").description("Title of own expense"),
                                fieldWithPath("amount").description("Amount of money spent"),
                                fieldWithPath("created").description("Date of own expense creation"),
                                fieldWithPath("receiptPhoto").description("Path to the receipt photo"),
                                fieldWithPath("currencyAbbreviation").description("The currency used for expense")
                        )));
    }

}
