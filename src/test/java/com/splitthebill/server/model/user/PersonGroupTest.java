package com.splitthebill.server.model.user;

import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.expense.GroupExpense;
import com.splitthebill.server.model.expense.PersonGroupExpense;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PersonGroupTest {

    @Test
    public void testGetSettleUpExpenses() {
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
        List<GroupExpense> expensesToSettleUp = johnMember.getSettleUpExpenses();
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
        GroupExpense johnToHenryActualExpense = expensesToSettleUp.stream()
                .filter(groupExpense -> groupExpense.getCurrency().getAbbreviation().equals("USD"))
                .findFirst()
                .get();
        GroupExpense johnToJanetActualExpense = expensesToSettleUp.stream()
                .filter(groupExpense -> groupExpense.getCurrency().getAbbreviation().equals("EUR"))
                .findFirst()
                .get();
        assertAll(() -> assertThat(johnToHenryActualExpense.getCurrency()).isEqualTo(johnToHenryExpectedExpense.getCurrency()),
                () -> assertThat(johnToHenryActualExpense.getAmount()).isEqualTo(johnToHenryExpectedExpense.getAmount()),
                () -> assertThat(johnToHenryActualExpense.getCreditor()).isEqualTo(johnToHenryExpectedExpense.getCreditor()),
                () -> assertThat(johnToHenryActualExpense.getPersonGroupExpenses()).hasSameSizeAs(johnToHenryExpectedExpense.getPersonGroupExpenses()),

                () -> assertThat(johnToJanetActualExpense.getCurrency()).isEqualTo(johnToJanetExpectedExpense.getCurrency()),
                () -> assertThat(johnToJanetActualExpense.getAmount()).isEqualTo(johnToJanetExpectedExpense.getAmount()),
                () -> assertThat(johnToJanetActualExpense.getCreditor()).isEqualTo(johnToJanetExpectedExpense.getCreditor()),
                () -> assertThat(johnToJanetActualExpense.getPersonGroupExpenses()).hasSameSizeAs(johnToJanetExpectedExpense.getPersonGroupExpenses())
        );
    }
}
