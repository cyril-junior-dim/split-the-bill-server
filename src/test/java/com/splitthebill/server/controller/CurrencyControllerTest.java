package com.splitthebill.server.controller;


import com.splitthebill.server.model.Currency;
import com.splitthebill.server.repository.CurrencyRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs(value = "target/generated-snippets/currency",
        uriScheme = "https",
        uriHost = "softstone.pl",
        uriPort = 8443)
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "splitthebill.app.scheduling-enabled=false")
public class CurrencyControllerTest {

    @MockBean
    private CurrencyRepository currencyRepository;

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser
    @Test
    public void testGetAllCurrencies() throws Exception {
        Currency euro = Currency.builder()
                .id(1L)
                .abbreviation("EUR")
                .exchangeRate(new BigDecimal("1"))
                .build();
        Currency americanDollar = Currency.builder()
                .id(2L)
                .abbreviation("USD")
                .exchangeRate(new BigDecimal("0.85"))
                .build();
        when(currencyRepository.findAll()).thenReturn(List.of(euro, americanDollar));
        mockMvc.perform(get("/currencies"))
                .andExpect(status().isOk())
                .andDo(document("get-all-currencies",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("Id of a currency"),
                                fieldWithPath("[].abbreviation").description("Abbreviation of a currency"),
                                fieldWithPath("[].exchangeRate").description("Exchange rate with relation to EUR.")
                        )));
    }

}
