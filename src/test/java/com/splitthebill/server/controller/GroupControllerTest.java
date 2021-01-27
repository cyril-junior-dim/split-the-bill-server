package com.splitthebill.server.controller;

import com.splitthebill.server.dto.group.GroupCreateDto;
import com.splitthebill.server.dto.group.GroupExpenseCreateDto;
import com.splitthebill.server.dto.group.GroupReadDto;
import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.expense.GroupExpense;
import com.splitthebill.server.model.expense.PersonGroupExpense;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.PersonGroup;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.GroupService;
import lombok.NonNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs(value = "target/generated-snippets/groups",
        uriScheme = "https",
        uriHost = "softstone.pl",
        uriPort = 8443)
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
public class GroupControllerTest {

    @MockBean
    private GroupService groupService;
    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;


    @WithMockUser
    @Test
    public void testGetUserGroups() throws Exception {
        Person johnDoe = Person.builder()
                .id(1L)
                .name("John Doe")
                .userAccount(mock(UserAccount.class))
                .build();
        Group portugalTrip = Group.builder()
                .id(1L)
                .name("Portugal trip")
                .build();
        Group mikesBirthday = Group.builder()
                .id(2L)
                .name("Mike's birthday")
                .build();
        PersonGroup johnDoePortugalTrip = PersonGroup.builder()
                .id(1L)
                .person(johnDoe)
                .group(portugalTrip)
                .balances(Map.of(new Currency("EUR", new BigDecimal("1")), BigDecimal.ZERO))
                .build();
        PersonGroup johnDoeMikesBirthday = PersonGroup.builder()
                .id(2L)
                .person(johnDoe)
                .group(mikesBirthday)
                .balances(Map.of(new Currency("USD", new BigDecimal("0.85")), BigDecimal.ZERO))
                .build();
        johnDoe.setPersonGroups(List.of(johnDoePortugalTrip, johnDoeMikesBirthday));
        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(johnDoe);
        mockMvc.perform(get("/groups"))
                .andExpect(status().isOk())
                .andDo(document("get-all-user-groups",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].groupId")
                                        .description("The identifier of a group."),
                                fieldWithPath("[].groupMemberId")
                                        .description("The identifier of a person and group association."),
                                fieldWithPath("[].personId")
                                        .description("The identifier of a person."),
                                fieldWithPath("[].name")
                                        .description("Name of a group"),
                                subsectionWithPath("[].memberBalance")
                                        .description("User balance in different currencies in a group.")
                        )));
    }

    @WithMockUser
    @Test
    public void testCreateGroup() throws Exception {
        String postBody = "{" +
                "\"name\": \"Portugal trip\"," +
                "\"membersIds\": [1, 2]" +
                "}";
        Person johnDoe = Person.builder()
                .id(1L)
                .name("John Doe")
                .userAccount(mock(UserAccount.class))
                .build();
        Person janeDoe = Person.builder()
                .id(2L)
                .name("Jane Doe")
                .userAccount(mock(UserAccount.class))
                .build();
        List<Person> mockPersonRepository = List.of(johnDoe, janeDoe);
        when(groupService.createGroup(any(GroupCreateDto.class))).thenAnswer(
                p -> {
                    GroupCreateDto groupDto = p.getArgument(0, GroupCreateDto.class);
                    Group portugalTrip = Group.builder()
                            .id(1L)
                            .name(groupDto.name)
                            .expenses(new ArrayList<>())
                            .build();
                    Long memberId = 1L;
                    List<PersonGroup> groupMembers = new ArrayList<>();
                    for(Long id : groupDto.membersIds){
                        PersonGroup member = PersonGroup.builder()
                                .id(memberId)
                                .person(mockPersonRepository
                                        .stream()
                                        .filter(person -> person.getId().equals(id))
                                        .findFirst()
                                        .get())
                                .group(portugalTrip)
                                .balances(Map.of(new Currency("EUR", new BigDecimal("1")), BigDecimal.ZERO))
                                .build();
                        groupMembers.add(member);
                        memberId += 1;
                    }
                    portugalTrip.setMembers(groupMembers);
                    return portugalTrip;
                });
        mockMvc.perform(post("/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postBody))
                .andExpect(status().isOk())
                .andDo(document("create-group",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name")
                                        .description("Name to be given to a group."),
                                subsectionWithPath("membersIds")
                                        .description("List of peoples' ids that are to be added to the group")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Id of a group"),
                                fieldWithPath("name").description("Name of a group"),
                                fieldWithPath("photoPath").description("Path to the group photo"),
                                subsectionWithPath("members").description("List of group members"),
                                subsectionWithPath("expenses").description("List of group expenses")
                        )));
    }

    @WithMockUser
    @Test
    public void testGetGroupById() throws Exception {
        Person johnDoe = Person.builder()
                .id(1L)
                .name("John Doe")
                .userAccount(mock(UserAccount.class))
                .build();
        Person janeDoe = Person.builder()
                .id(2L)
                .name("Jane Doe")
                .userAccount(mock(UserAccount.class))
                .build();
        Group portugalTrip = Group.builder()
                .id(1L)
                .name("Portugal Trip")
                .expenses(new ArrayList<>())
                .build();
        PersonGroup johnMember = PersonGroup.builder()
                .id(1L)
                .person(johnDoe)
                .group(portugalTrip)
                .balances(Map.of(new Currency("EUR", new BigDecimal("1")), BigDecimal.ZERO))
                .build();
        PersonGroup janeMember = PersonGroup.builder()
                .id(2L)
                .person(janeDoe)
                .group(portugalTrip)
                .balances(Map.of(new Currency("EUR", new BigDecimal("1")), BigDecimal.ZERO))
                .build();
        johnDoe.setPersonGroups(List.of(johnMember));
        janeDoe.setPersonGroups(List.of(janeMember));
        portugalTrip.setMembers(List.of(johnMember, janeMember));

        GroupExpense flight = GroupExpense.builder()
                .id(1L)
                .creditor(johnMember)
                .group(portugalTrip)
                .build();
        PersonGroupExpense johnDebt = PersonGroupExpense.builder()
                .id(1L)
                .debtor(johnMember)
                .expense(flight)
                .weight(1)
                .isReviewed(true)
                .build();
        PersonGroupExpense janeDebt = PersonGroupExpense.builder()
                .id(2L)
                .debtor(janeMember)
                .expense(flight)
                .weight(1)
                .isReviewed(true)
                .build();
        flight.setPersonGroupExpenses(List.of(johnDebt, janeDebt));
        flight.setAmount(new BigDecimal("40"));
        flight.setCurrency(new Currency("EUR", new BigDecimal("1")));
        flight.setTitle("Flight");
        portugalTrip.setExpenses(List.of(flight));

        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(johnDoe);
        when(groupService.getGroupById(1L)).thenReturn(portugalTrip);
        mockMvc.perform(get("/groups/{id}", 1))
                .andExpect(status().isOk())
                .andDo(document("get-group-by-id",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("Id of a group to be fetched.")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Id of a group"),
                                fieldWithPath("name").description("Name of a group"),
                                fieldWithPath("photoPath").description("Path to the group photo"),

                                fieldWithPath("members[].groupId").description("The identifier of a group."),
                                fieldWithPath("members[].groupMemberId").description("The identifier of a person and group association."),
                                fieldWithPath("members[].personId").description("The identifier of a person."),
                                fieldWithPath("members[].name").description("Name of a group"),
                                subsectionWithPath("members[].memberBalance").description("User balance in different currencies in a group."),

                                fieldWithPath("expenses[].expenseId").description("Id of an expense"),
                                fieldWithPath("expenses[].creditorMemberId").description("Id of a creditor (personGroup id)"),
                                fieldWithPath("expenses[].title").description("Title of an expense"),
                                subsectionWithPath("expenses[].debtorMemberIds").description("Ids of group members that owe the creditor."),
                                fieldWithPath("expenses[].amount").description("The amount of an expense"),
                                fieldWithPath("expenses[].currency").description("Currency abbreviation used for expense")
                        )));
    }


    @WithMockUser
    @Test
    public void testAddGroupMember() throws Exception {
        Person johnDoe = Person.builder()
                .id(1L)
                .name("John Doe")
                .userAccount(mock(UserAccount.class))
                .build();
        Person janeDoe = Person.builder()
                .id(2L)
                .name("Jane Doe")
                .userAccount(mock(UserAccount.class))
                .build();
        Group portugalTrip = Group.builder()
                .id(1L)
                .name("Portugal Trip")
                .expenses(new ArrayList<>())
                .build();
        PersonGroup johnMember = PersonGroup.builder()
                .id(1L)
                .person(johnDoe)
                .group(portugalTrip)
                .balances(Map.of(new Currency("EUR", new BigDecimal("1")), BigDecimal.ZERO))
                .build();

        johnDoe.setPersonGroups(new ArrayList<>(List.of(johnMember)));
        portugalTrip.setMembers(new ArrayList<>(List.of(johnMember)));

        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(johnDoe);
        doNothing().when(groupService).addGroupMember(1L, 2L);
        mockMvc.perform(patch("/groups/{groupId}/add?personId={personId}", 1, 2))
                .andExpect(status().isOk())
                .andDo(document("add-group-member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("groupId").description("Id of a group")
                        ),
                        requestParameters(
                                parameterWithName("personId").description("Id of a person")
                        )));
    }

    @WithMockUser
    @Test
    public void testCreateExpense() throws Exception {
        Person johnDoe = Person.builder()
                .id(1L)
                .name("John Doe")
                .userAccount(mock(UserAccount.class))
                .build();
        Group portugalTrip = Group.builder()
                .id(1L)
                .name("Portugal Trip")
                .expenses(new ArrayList<>())
                .build();
        PersonGroup johnMember = PersonGroup.builder()
                .id(1L)
                .person(johnDoe)
                .group(portugalTrip)
                .balances(Map.of(new Currency("EUR", new BigDecimal("1")), BigDecimal.ZERO))
                .build();

        String postBody = "{" +
                "\"title\": \"Domino's Pizza\"," +
                "\"creditorId\": \"1\"," +
                "\"debtors\": [" +
                "{" +
                "\"debtorId\": 1," +
                "\"weight\": 1" +
                "}," +
                "{" +
                "\"debtorId\": 2," +
                "\"weight\": 2" +
                "}]," +
                "\"amount\": 30," +
                "\"currencyAbbreviation\": \"EUR\"" +
                "}";

        johnDoe.setPersonGroups(new ArrayList<>(List.of(johnMember)));
        portugalTrip.setMembers(new ArrayList<>(List.of(johnMember)));

        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(johnDoe);
        doNothing().when(groupService).addExpense(1L, mock(GroupExpenseCreateDto.class));

        mockMvc.perform(post("/groups/{groupId}/expenses", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(postBody))
                .andExpect(status().isOk())
                .andDo(document("create-group-expense",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("groupId").description("Id of a group")
                        ),
                        requestFields(
                                fieldWithPath("title").description("Title of an expense"),
                                fieldWithPath("creditorId").description("Id of a group member that is a creditor"),
                                fieldWithPath("debtors[].debtorId").description("Id of a group member that is a debtor"),
                                fieldWithPath("debtors[].weight")
                                        .description("Weight describing what part of an expense this debtor should cover"),
                                fieldWithPath("amount").description("The amount creditor payed"),
                                fieldWithPath("currencyAbbreviation").description("Abbreviation of the currency used for expense")
                        )));
    }


    @WithMockUser
    @Test
    public void testGetSettleUpExpenses() throws Exception {
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
        Person henry = Person.builder()
                .id(3L)
                .name("Henry Marks")
                .balances(Map.of(americanDollar, BigDecimal.ZERO))
                .preferredCurrency(americanDollar)
                .build();
        Group holidays = Group.builder()
                .id(1L)
                .name("Holidays in France")
                .build();
        PersonGroup johnMember = PersonGroup.builder()
                .id(1L)
                .person(john)
                .group(holidays)
                .balances(Map.of(americanDollar, new BigDecimal("-10"), euro, new BigDecimal("-3.30")))
                .build();
        PersonGroup janetMember = PersonGroup.builder()
                .id(2L)
                .person(janet)
                .group(holidays)
                .balances(Map.of(americanDollar, new BigDecimal("0"), euro, new BigDecimal("6.60")))
                .build();
        PersonGroup henryMember = PersonGroup.builder()
                .id(3L)
                .person(henry)
                .group(holidays)
                .balances(Map.of(americanDollar, new BigDecimal("10"), euro, new BigDecimal("-3.30")))
                .build();
        holidays.setMembers(List.of(johnMember, janetMember, henryMember));
        john.setPersonGroups(List.of(johnMember));
        janet.setPersonGroups(List.of(janetMember));
        henry.setPersonGroups(List.of(henryMember));
        GroupExpense johnToJanetExpectedExpense = GroupExpense.builder()
                .creditor(johnMember)
                .group(holidays)
                .build();
        johnToJanetExpectedExpense.setCurrency(euro);
        johnToJanetExpectedExpense.setTitle("Settle up");
        johnToJanetExpectedExpense.setAmount(new BigDecimal("3.30"));
        PersonGroupExpense janetDebtor = PersonGroupExpense.builder()
                .debtor(janetMember)
                .expense(johnToJanetExpectedExpense)
                .weight(1)
                .build();
        johnToJanetExpectedExpense.setPersonGroupExpenses(List.of(janetDebtor));

        GroupExpense johnToHenryExpectedExpense = GroupExpense.builder()
                .creditor(johnMember)
                .group(holidays)
                .build();
        johnToHenryExpectedExpense.setCurrency(americanDollar);
        johnToHenryExpectedExpense.setTitle("Settle up");
        johnToHenryExpectedExpense.setAmount(new BigDecimal("10"));
        PersonGroupExpense henryDebtor = PersonGroupExpense.builder()
                .debtor(henryMember)
                .expense(johnToHenryExpectedExpense)
                .weight(1)
                .build();
        johnToHenryExpectedExpense.setPersonGroupExpenses(List.of(henryDebtor));

        when(jwtUtils.getPersonFromAuthentication(any(Authentication.class))).thenReturn(john);
        mockMvc.perform(get("/groups/{groupId}/settleUpExpenses", 1))
                .andExpect(status().isOk())
                .andDo(document("get-group-settle-up-expenses",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("groupId").description("Id of a group user want to settle up in.")
                        ),
                        responseFields(
                                fieldWithPath("[].title").description("Title of an expense"),
                                fieldWithPath("[].creditorId").description("Id of a group member that is a creditor"),
                                fieldWithPath("[].debtors[].debtorId").description("Id of a group member that is a debtor"),
                                fieldWithPath("[].debtors[].weight")
                                        .description("Weight describing what part of an expense this debtor should cover"),
                                fieldWithPath("[].amount").description("The amount creditor payed"),
                                fieldWithPath("[].currencyAbbreviation").description("Abbreviation of the currency used for expense")
                        )));
    }

}
