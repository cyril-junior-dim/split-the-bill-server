package com.splitthebill.server.model.user;

import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.expense.GroupExpense;
import com.splitthebill.server.model.expense.PersonGroupExpense;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class PersonGroupTest {

    @Test
    public void testBalanceAfterAddExpense() {
        Currency euro = Currency.builder()
                .id(1L)
                .abbreviation("EUR")
                .exchangeRate(BigDecimal.ONE)
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
                .balances(Map.of(euro, BigDecimal.ZERO))
                .preferredCurrency(euro)
                .build();
        Group holidays = new Group();
        holidays.setName("Holidays in France");
        var startBalance = new HashMap<Currency, BigDecimal>();
        startBalance.put(euro, BigDecimal.ZERO);
        PersonGroup johnMember = PersonGroup.builder()
                .id(1L)
                .person(john)
                .group(holidays)
                .balances(startBalance)
                .build();
        PersonGroup janetMember = PersonGroup.builder()
                .id(2L)
                .person(janet)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        PersonGroup henryMember = PersonGroup.builder()
                .id(3L)
                .person(henry)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        holidays.setMembers(List.of(johnMember, janetMember, henryMember));
        GroupExpense expense = new GroupExpense();
        expense.setTitle("Pizza at the Beach");
        expense.setAmount(BigDecimal.valueOf(60));
        expense.setCreditor(johnMember);
        expense.setCurrency(euro);
        List<PersonGroupExpense> participants = List.of(
                PersonGroupExpense.builder().debtor(johnMember).expense(expense).weight(1).build(),
                PersonGroupExpense.builder().debtor(janetMember).expense(expense).weight(1).build(),
                PersonGroupExpense.builder().debtor(henryMember).expense(expense).weight(1).build()
        );
        expense.setPersonGroupExpenses(participants);
        holidays.addExpense(expense);

        assertAll(
                () -> assertThat(johnMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(40).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(janetMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(-20).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(henryMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(-20).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(holidays.getMembers().stream().map(x -> x.getBalances().get(euro)).reduce(BigDecimal::add).get())
                        .isEqualTo(BigDecimal.ZERO.setScale(2))
        );
    }

    @Test
    public void testBalanceAfterAddExpense2(){
        Currency euro = Currency.builder()
                .id(1L)
                .abbreviation("EUR")
                .exchangeRate(BigDecimal.ONE)
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
                .balances(Map.of(euro, BigDecimal.ZERO))
                .preferredCurrency(euro)
                .build();
        Group holidays = new Group();
        holidays.setName("Holidays in France");
        var startBalance = new HashMap<Currency, BigDecimal>();
        startBalance.put(euro, BigDecimal.ZERO);
        PersonGroup johnMember = PersonGroup.builder()
                .id(1L)
                .person(john)
                .group(holidays)
                .balances(startBalance)
                .build();
        PersonGroup janetMember = PersonGroup.builder()
                .id(2L)
                .person(janet)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        PersonGroup henryMember = PersonGroup.builder()
                .id(3L)
                .person(henry)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        holidays.setMembers(List.of(johnMember, janetMember, henryMember));
        GroupExpense expense = new GroupExpense();
        expense.setTitle("Drinks at the disco");
        expense.setAmount(BigDecimal.valueOf(20));
        expense.setCreditor(henryMember);
        expense.setCurrency(euro);
        List<PersonGroupExpense> participants2 = List.of(
                PersonGroupExpense.builder().debtor(johnMember).expense(expense).weight(1).build(),
                PersonGroupExpense.builder().debtor(janetMember).expense(expense).weight(1).build(),
                PersonGroupExpense.builder().debtor(henryMember).expense(expense).weight(1).build()
        );
        expense.setPersonGroupExpenses(participants2);
        holidays.addExpense(expense);

        assertAll(
                () -> assertThat(johnMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(-6.67).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(janetMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(-6.67).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(henryMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(13.34).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(holidays.getMembers().stream().map(x -> x.getBalances().get(euro)).reduce(BigDecimal::add).get())
                        .isEqualTo(BigDecimal.ZERO.setScale(2))
        );
    }

    @Test
    public void testBalanceAfterAddExpenseWithoutOneMember(){
        Currency euro = Currency.builder()
                .id(1L)
                .abbreviation("EUR")
                .exchangeRate(BigDecimal.ONE)
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
                .balances(Map.of(euro, BigDecimal.ZERO))
                .preferredCurrency(euro)
                .build();
        Group holidays = new Group();
        holidays.setName("Holidays in France");
        var startBalance = new HashMap<Currency, BigDecimal>();
        startBalance.put(euro, BigDecimal.ZERO);
        PersonGroup johnMember = PersonGroup.builder()
                .id(1L)
                .person(john)
                .group(holidays)
                .balances(startBalance)
                .build();
        PersonGroup janetMember = PersonGroup.builder()
                .id(2L)
                .person(janet)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        PersonGroup henryMember = PersonGroup.builder()
                .id(3L)
                .person(henry)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        holidays.setMembers(List.of(johnMember, janetMember, henryMember));
        GroupExpense expense = new GroupExpense();
        expense.setTitle("Drinks at the disco");
        expense.setAmount(BigDecimal.valueOf(40));
        expense.setCreditor(janetMember);
        expense.setCurrency(euro);
        List<PersonGroupExpense> participants = List.of(
                PersonGroupExpense.builder().debtor(janetMember).expense(expense).weight(1).build(),
                PersonGroupExpense.builder().debtor(henryMember).expense(expense).weight(1).build()
        );
        expense.setPersonGroupExpenses(participants);
        holidays.addExpense(expense);

        assertAll(
                () -> assertThat(johnMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(0)),
                () -> assertThat(janetMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(henryMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(-20).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(holidays.getMembers().stream().map(x -> x.getBalances().get(euro)).reduce(BigDecimal::add).get())
                        .isEqualTo(BigDecimal.ZERO.setScale(2))
        );
    }

    @Test
    public void testBalanceAfterAddExpenseWithDifferentWeights(){
        Currency euro = Currency.builder()
                .id(1L)
                .abbreviation("EUR")
                .exchangeRate(BigDecimal.ONE)
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
                .balances(Map.of(euro, BigDecimal.ZERO))
                .preferredCurrency(euro)
                .build();
        Group holidays = new Group();
        holidays.setName("Holidays in France");
        var startBalance = new HashMap<Currency, BigDecimal>();
        startBalance.put(euro, BigDecimal.ZERO);
        PersonGroup johnMember = PersonGroup.builder()
                .id(1L)
                .person(john)
                .group(holidays)
                .balances(startBalance)
                .build();
        PersonGroup janetMember = PersonGroup.builder()
                .id(2L)
                .person(janet)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        PersonGroup henryMember = PersonGroup.builder()
                .id(3L)
                .person(henry)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        holidays.setMembers(List.of(johnMember, janetMember, henryMember));
        GroupExpense expense = new GroupExpense();
        expense.setTitle("Pizza Marinara Extra Big");
        expense.setAmount(BigDecimal.valueOf(40));
        expense.setCreditor(johnMember);
        expense.setCurrency(euro);
        List<PersonGroupExpense> participants = List.of(
                PersonGroupExpense.builder().debtor(johnMember).expense(expense).weight(3).build(),
                PersonGroupExpense.builder().debtor(janetMember).expense(expense).weight(1).build(),
                PersonGroupExpense.builder().debtor(henryMember).expense(expense).weight(4).build()
        );
        expense.setPersonGroupExpenses(participants);
        holidays.addExpense(expense);

        assertAll(
                () -> assertThat(johnMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(25).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(janetMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(-5).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(henryMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(-20).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(holidays.getMembers().stream().map(x -> x.getBalances().get(euro)).reduce(BigDecimal::add).get())
                        .isEqualTo(BigDecimal.ZERO.setScale(2))
        );
    }

    @Test
    public void testBalanceAfterAddExpenseWithDifferentWeightsAndWithoutOneMember(){
        Currency euro = Currency.builder()
                .id(1L)
                .abbreviation("EUR")
                .exchangeRate(BigDecimal.ONE)
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
                .balances(Map.of(euro, BigDecimal.ZERO))
                .preferredCurrency(euro)
                .build();
        Group holidays = new Group();
        holidays.setName("Holidays in France");
        var startBalance = new HashMap<Currency, BigDecimal>();
        startBalance.put(euro, BigDecimal.ZERO);
        PersonGroup johnMember = PersonGroup.builder()
                .id(1L)
                .person(john)
                .group(holidays)
                .balances(startBalance)
                .build();
        PersonGroup janetMember = PersonGroup.builder()
                .id(2L)
                .person(janet)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        PersonGroup henryMember = PersonGroup.builder()
                .id(3L)
                .person(henry)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        holidays.setMembers(List.of(johnMember, janetMember, henryMember));
        GroupExpense expense = new GroupExpense();
        expense.setTitle("Bag of Candies (7 total)");
        expense.setAmount(BigDecimal.valueOf(6));
        expense.setCreditor(johnMember);
        expense.setCurrency(euro);
        List<PersonGroupExpense> participants = List.of(
                PersonGroupExpense.builder().debtor(johnMember).expense(expense).weight(3).build(),
                PersonGroupExpense.builder().debtor(henryMember).expense(expense).weight(4).build()
        );
        expense.setPersonGroupExpenses(participants);
        holidays.addExpense(expense);

        assertAll(
                () -> assertThat(johnMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(6-2.57).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(janetMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.ZERO),
                () -> assertThat(henryMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(-3.43).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(holidays.getMembers().stream().map(x -> x.getBalances().get(euro)).reduce(BigDecimal::add).get())
                        .isEqualTo(BigDecimal.ZERO.setScale(2))
        );
    }

    @Test
    public void testBalanceAfterCreatingAndDeletingExpense(){
        Currency euro = Currency.builder()
                .id(1L)
                .abbreviation("EUR")
                .exchangeRate(BigDecimal.ONE)
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
                .balances(Map.of(euro, BigDecimal.ZERO))
                .preferredCurrency(euro)
                .build();
        Group holidays = new Group();
        holidays.setName("Holidays in France");
        var startBalance = new HashMap<Currency, BigDecimal>();
        startBalance.put(euro, BigDecimal.ZERO);
        PersonGroup johnMember = PersonGroup.builder()
                .id(1L)
                .person(john)
                .group(holidays)
                .balances(startBalance)
                .build();
        PersonGroup janetMember = PersonGroup.builder()
                .id(2L)
                .person(janet)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        PersonGroup henryMember = PersonGroup.builder()
                .id(3L)
                .person(henry)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        holidays.setMembers(List.of(johnMember, janetMember, henryMember));
        GroupExpense expense = new GroupExpense();
        expense.setTitle("Drinks at the disco");
        expense.setAmount(BigDecimal.valueOf(20));
        expense.setCreditor(henryMember);
        expense.setCurrency(euro);
        List<PersonGroupExpense> participants = List.of(
                PersonGroupExpense.builder().debtor(johnMember).expense(expense).weight(1).build(),
                PersonGroupExpense.builder().debtor(janetMember).expense(expense).weight(1).build(),
                PersonGroupExpense.builder().debtor(henryMember).expense(expense).weight(1).build()
        );
        expense.setPersonGroupExpenses(participants);
        holidays.addExpense(expense);

        assertAll(
                () -> assertThat(johnMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(-6.67).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(janetMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(-6.67).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(henryMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(13.34).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(holidays.getMembers().stream().map(x -> x.getBalances().get(euro)).reduce(BigDecimal::add).get())
                        .isEqualTo(BigDecimal.ZERO.setScale(2))
        );
        holidays.deleteExpense(expense);
        assertAll(
                () -> assertThat(johnMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.ZERO.setScale(2)),
                () -> assertThat(janetMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.ZERO.setScale(2)),
                () -> assertThat(henryMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.ZERO.setScale(2)),
                () -> assertThat(holidays.getMembers().stream().map(x -> x.getBalances().get(euro)).reduce(BigDecimal::add).get())
                        .isEqualTo(BigDecimal.ZERO.setScale(2))
        );
    }

    @Test
    public void testBalanceAfterAddExpenseWithDifferentWeightsAndWithoutOneMemberAndThenDelete(){
        Currency euro = Currency.builder()
                .id(1L)
                .abbreviation("EUR")
                .exchangeRate(BigDecimal.ONE)
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
                .balances(Map.of(euro, BigDecimal.ZERO))
                .preferredCurrency(euro)
                .build();
        Group holidays = new Group();
        holidays.setName("Holidays in France");
        var startBalance = new HashMap<Currency, BigDecimal>();
        startBalance.put(euro, BigDecimal.ZERO);
        PersonGroup johnMember = PersonGroup.builder()
                .id(1L)
                .person(john)
                .group(holidays)
                .balances(startBalance)
                .build();
        PersonGroup janetMember = PersonGroup.builder()
                .id(2L)
                .person(janet)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        PersonGroup henryMember = PersonGroup.builder()
                .id(3L)
                .person(henry)
                .group(holidays)
                .balances((HashMap<Currency, BigDecimal>)startBalance.clone())
                .build();
        holidays.setMembers(List.of(johnMember, janetMember, henryMember));
        GroupExpense expense = new GroupExpense();
        expense.setTitle("Bag of Candies (7 total)");
        expense.setAmount(BigDecimal.valueOf(6));
        expense.setCreditor(johnMember);
        expense.setCurrency(euro);
        List<PersonGroupExpense> participants = List.of(
                PersonGroupExpense.builder().debtor(johnMember).expense(expense).weight(3).build(),
                PersonGroupExpense.builder().debtor(henryMember).expense(expense).weight(4).build()
        );
        expense.setPersonGroupExpenses(participants);
        holidays.addExpense(expense);

        assertAll(
                () -> assertThat(johnMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(6-2.57).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(janetMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.ZERO),
                () -> assertThat(henryMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.valueOf(-3.43).setScale(2, RoundingMode.HALF_UP)),
                () -> assertThat(holidays.getMembers().stream().map(x -> x.getBalances().get(euro)).reduce(BigDecimal::add).get())
                        .isEqualTo(BigDecimal.ZERO.setScale(2))
        );

        holidays.deleteExpense(expense);
        assertAll(
                () -> assertThat(johnMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.ZERO.setScale(2)),
                () -> assertThat(janetMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.ZERO),
                () -> assertThat(henryMember.getBalances().get(euro))
                        .isEqualTo(BigDecimal.ZERO.setScale(2)),
                () -> assertThat(holidays.getMembers().stream().map(x -> x.getBalances().get(euro)).reduce(BigDecimal::add).get())
                        .isEqualTo(BigDecimal.ZERO.setScale(2))
        );
    }

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

                () ->  assertThat(johnToJanetActualExpense.getCurrency()).isEqualTo(johnToJanetExpectedExpense.getCurrency()),
                () -> assertThat(johnToJanetActualExpense.getAmount()).isEqualTo(johnToJanetExpectedExpense.getAmount()),
                () -> assertThat(johnToJanetActualExpense.getCreditor()).isEqualTo(johnToJanetExpectedExpense.getCreditor()),
                () -> assertThat(johnToJanetActualExpense.getPersonGroupExpenses()).hasSameSizeAs(johnToJanetExpectedExpense.getPersonGroupExpenses())
        );
    }
}
