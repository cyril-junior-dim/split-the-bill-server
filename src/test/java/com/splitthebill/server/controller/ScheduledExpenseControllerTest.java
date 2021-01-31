package com.splitthebill.server.controller;

import com.splitthebill.server.dto.expense.scheduled.own.ScheduledOwnExpenseCreateDto;
import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.expense.scheduled.FrequencyUnit;
import com.splitthebill.server.model.expense.scheduled.Schedule;
import com.splitthebill.server.model.expense.scheduled.group.ScheduledGroupExpense;
import com.splitthebill.server.model.expense.scheduled.group.ScheduledPersonGroupExpense;
import com.splitthebill.server.model.expense.scheduled.own.ScheduledOwnExpense;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.PersonGroup;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.ScheduledExpenseService;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs(value = "target/generated-snippets/scheduled-expense",
        uriScheme = "https",
        uriHost = "softstone.pl",
        uriPort = 8443)
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.scheduling.enable=false")
public class ScheduledExpenseControllerTest {

    @MockBean
    private JwtUtils jwtUtils;
    @MockBean
    private ScheduledExpenseService scheduledExpenseService;

    @Autowired
    private MockMvc mockMvc;


    @WithMockUser
    @Test
    public void testGetAllScheduledExpenses() throws Exception {
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

        Person john = Person.builder()
                .id(1L)
                .name("John Doe")
                .balances(Map.of(euro, BigDecimal.ZERO))
                .preferredCurrency(euro)
                .build();
        Person janet = Person.builder()
                .id(2L)
                .name("Janet Doe")
                .balances(Map.of(euro, BigDecimal.ZERO))
                .preferredCurrency(euro)
                .build();
        Group roommates = Group.builder()
                .id(1L)
                .name("Roommates")
                .build();
        PersonGroup johnMember = PersonGroup.builder()
                .id(1L)
                .person(john)
                .group(roommates)
                .balances(Map.of(americanDollar, new BigDecimal("-10"), euro, new BigDecimal("-3.30")))
                .build();
        PersonGroup janetMember = PersonGroup.builder()
                .id(2L)
                .person(janet)
                .group(roommates)
                .balances(Map.of(americanDollar, new BigDecimal("0"), euro, new BigDecimal("6.60")))
                .build();
        roommates.setMembers(List.of(johnMember, janetMember));
        john.setPersonGroups(List.of(johnMember));
        janet.setPersonGroups(List.of(janetMember));

        Schedule everyMonth = Schedule.builder()
                .id(1L)
                .amount(1)
                .frequencyUnit(FrequencyUnit.MONTH)
                .nextTrigger(LocalDate.parse("01-02-2021", DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .build();

        ScheduledGroupExpense scheduledFlatRent = ScheduledGroupExpense.builder()
                .id(1L)
                .creditor(johnMember)
                .group(roommates)
                .schedule(everyMonth)
                .build();
        scheduledFlatRent.setCurrency(euro);
        scheduledFlatRent.setTitle("Flat rent");
        scheduledFlatRent.setAmount(new BigDecimal("500"));
        ScheduledPersonGroupExpense janetDebtor = ScheduledPersonGroupExpense.builder()
                .debtor(janetMember)
                .scheduledGroupExpense(scheduledFlatRent)
                .weight(1)
                .build();
        ScheduledPersonGroupExpense johnDebtor = ScheduledPersonGroupExpense.builder()
                .debtor(johnMember)
                .scheduledGroupExpense(scheduledFlatRent)
                .weight(1)
                .build();
        scheduledFlatRent.setScheduledPersonGroupExpenses(List.of(johnDebtor, janetDebtor));

        Schedule everyTwoWeeks = Schedule.builder()
                .id(2L)
                .amount(2)
                .frequencyUnit(FrequencyUnit.WEEK)
                .nextTrigger(LocalDate.parse("01-02-2021", DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .build();
        ScheduledOwnExpense scheduledGymMembership = ScheduledOwnExpense.builder()
                .id(1L)
                .person(john)
                .schedule(everyTwoWeeks)
                .build();
        scheduledGymMembership.setAmount(new BigDecimal("9.99"));
        scheduledGymMembership.setCurrency(americanDollar);
        scheduledGymMembership.setTitle("Gym membership");

        john.setScheduledOwnExpenses(List.of(scheduledGymMembership));

        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(john);
        when(scheduledExpenseService.getAllScheduledGroupExpenses(john)).thenReturn(List.of(scheduledFlatRent));
        when(scheduledExpenseService.getAllScheduledOwnExpenses(john)).thenReturn(List.of(scheduledGymMembership));

        mockMvc.perform(get("/scheduledExpenses"))
                .andExpect(status().isOk())
                .andDo(document("get-all-scheduled-expenses",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("group[].id").description("Id of a scheduled group expense"),
                                fieldWithPath("group[].groupId").description("Id of a group to which the epxense belongs"),
                                fieldWithPath("group[].groupName").description("Name of a group"),
                                fieldWithPath("group[].title").description("Title of the scheduled group expense"),
                                fieldWithPath("group[].amount").description("The amount to be spent"),
                                fieldWithPath("group[].currency").description("Currency used for expense"),
                                fieldWithPath("group[].debtors[].debtorId").description("The id of a debtor. Relates to group member id (PersonGroup)"),
                                fieldWithPath("group[].debtors[].weight").description("Weight describing what part of an expense this debtor should cover"),
                                fieldWithPath("group[].debtors[].name").description("The name of a group member."),
                                fieldWithPath("group[].schedule.amount").description("Number of frequency units to pass until next trigger"),
                                fieldWithPath("group[].schedule.frequencyUnit").description("Frequency unit e.g. DAY, MONTH"),
                                fieldWithPath("group[].schedule.nextTrigger").description("The date describing when the expense will be added"),

                                fieldWithPath("own[].id").description("Id of a scheduled own expense"),
                                fieldWithPath("own[].title").description("Title of the scheduled own expense"),
                                fieldWithPath("own[].amount").description("The amount to be spent"),
                                fieldWithPath("own[].currency").description("Currency used for expense"),
                                fieldWithPath("own[].schedule.amount").description("Number of frequency units to pass until next trigger"),
                                fieldWithPath("own[].schedule.frequencyUnit").description("Frequency unit e.g. DAY, MONTH"),
                                fieldWithPath("own[].schedule.nextTrigger").description("The date describing when the expense will be added")
                        )
                ));
    }

    @WithMockUser
    @Test
    public void testScheduleOwnExpense() throws Exception {
        String postBody = "{" +
                "    \"title\": \"Some scheduled expense\"," +
                "    \"amount\": 60," +
                "    \"currency\": \"EUR\"," +
                "    \"schedule\": {" +
                "        \"amount\": 3," +
                "        \"frequencyUnit\": \"WEEK\"," +
                "        \"nextTrigger\": \"10-02-2021\"" +
                "    }" +
                "}";
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
        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(john);
        when(scheduledExpenseService.scheduledOwnExpense(any(Person.class), any(ScheduledOwnExpenseCreateDto.class))).thenAnswer(
                p -> {
                    ScheduledOwnExpenseCreateDto createDto = p.getArgument(1, ScheduledOwnExpenseCreateDto.class);
                    ScheduledOwnExpense scheduledOwnExpense = new ScheduledOwnExpense();
                    scheduledOwnExpense.setCurrency(euro);
                    scheduledOwnExpense.setPerson(john);
                    BigDecimal amount = createDto.amount;
                    scheduledOwnExpense.setAmount(amount);

                    FrequencyUnit frequencyUnit;
                    try {
                        frequencyUnit = FrequencyUnit.valueOf(createDto.schedule.frequencyUnit);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Incorrect frequency unit. Should be 'DAY', 'WEEK', 'MONTH' or 'YEAR'.");
                    }

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate nextTriggerDate;
                    try {
                        nextTriggerDate = LocalDate.parse(createDto.schedule.nextTrigger, formatter);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Next trigger date in wrong format. Required format: dd-MM-yyyy");
                    }

                    Schedule schedule = Schedule.builder()
                            .amount(createDto.schedule.amount)
                            .frequencyUnit(frequencyUnit)
                            .nextTrigger(nextTriggerDate)
                            .build();
                    scheduledOwnExpense.setId(1L);
                    scheduledOwnExpense.setTitle(createDto.title);
                    scheduledOwnExpense.setSchedule(schedule);
                    return scheduledOwnExpense;
                });

        mockMvc.perform(post("/scheduledExpenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postBody))
                .andExpect(status().isOk())
                .andDo(document("create-own-scheduled-expense",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").description("Title of a scheduled expense"),
                                fieldWithPath("amount").description("Amount to be spent"),
                                fieldWithPath("currency").description("Expense currency"),
                                fieldWithPath("schedule.amount").description("Number of frequency units to pass until next trigger"),
                                fieldWithPath("schedule.frequencyUnit").description("Frequency unit e.g. DAY, MONTH"),
                                fieldWithPath("schedule.nextTrigger").description("First trigger date in format dd-MM-yyy")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Id of a scheduled own expense"),
                                fieldWithPath("title").description("Title of a scheduled expense"),
                                fieldWithPath("amount").description("Amount to be spent"),
                                fieldWithPath("currency").description("Expense currency"),
                                fieldWithPath("schedule.amount").description("Number of frequency units to pass until next trigger"),
                                fieldWithPath("schedule.frequencyUnit").description("Frequency unit e.g. DAY, MONTH"),
                                fieldWithPath("schedule.nextTrigger").description("The date describing when the expense will be added")
                        )));
    }


    @WithMockUser
    @Test
    public void testDeleteScheduledExpense() throws Exception {
        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(mock(Person.class));
        doNothing().when(scheduledExpenseService).deleteScheduledOwnExpense(any(Long.class), any(Person.class));
        mockMvc.perform(delete("/scheduledExpenses/{expenseId}", 1))
                .andExpect(status().isNoContent())
                .andDo(document("delete-scheduled-own-expense",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("expenseId").description("Id of an expense to be deleted")
                        )));
    }
}
