package com.splitthebill.server.service;

import com.splitthebill.server.dto.expense.scheduled.group.ScheduledExpenseParticipantCreateDto;
import com.splitthebill.server.dto.expense.scheduled.group.ScheduledGroupExpenseCreateDto;
import com.splitthebill.server.dto.group.ExpenseParticipantCreateDto;
import com.splitthebill.server.dto.group.GroupCreateDto;
import com.splitthebill.server.dto.group.GroupExpenseCreateDto;
import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.expense.GroupExpense;
import com.splitthebill.server.model.expense.PersonGroupExpense;
import com.splitthebill.server.model.expense.scheduled.FrequencyUnit;
import com.splitthebill.server.model.expense.scheduled.Schedule;
import com.splitthebill.server.model.expense.scheduled.group.ScheduledGroupExpense;
import com.splitthebill.server.model.expense.scheduled.group.ScheduledPersonGroupExpense;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.PersonGroup;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.repository.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    @NonNull
    private final GroupRepository groupRepository;

    @NonNull
    private final GroupExpenseRepository groupExpenseRepository;

    @NonNull
    private final PersonService personService;

    @NonNull
    private final PersonGroupRepository personGroupRepository;

    @NonNull
    private final CurrencyRepository currencyRepository;

    @NonNull
    private final NotificationService notificationService;

    @NonNull
    private final ScheduledGroupExpenseRepository scheduledGroupExpenseRepository;


    public Group createGroup(GroupCreateDto groupDto) throws EntityNotFoundException {
        Group group = new Group();
        group.setName(groupDto.name);
        group = groupRepository.save(group);
        LinkedList<PersonGroup> members = new LinkedList<>();
        for (Long id : groupDto.membersIds) {
            Person person = personService.getPersonById(id);
            PersonGroup personGroup = new PersonGroup(person, group);
            personGroup = personGroupRepository.save(personGroup);
            members.add(personGroup);
        }
        group.setMembers(members);
        group = groupRepository.save(group);
        return group;
    }

    public Group getGroupById(Long id) throws EntityNotFoundException {
        return groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group has not been found."));
    }

    public void addExpense(Long groupId, GroupExpenseCreateDto expenseDto, Person issuer) throws EntityNotFoundException {
        GroupExpense groupExpense = new GroupExpense();
        Group group = getGroupById(groupId);
        Currency currency = currencyRepository.findCurrencyByAbbreviation(expenseDto.currencyAbbreviation)
                .orElseThrow(EntityNotFoundException::new);
        groupExpense.setGroup(group);
        groupExpense.setTitle(expenseDto.title);
        groupExpense.setCurrency(currency);
        PersonGroup creditor = personGroupRepository.findById(expenseDto.creditorId)
                .orElseThrow(() -> new EntityNotFoundException("Creditor has not been found"));
        if (!personGroupRepository.existsByIdAndGroup(expenseDto.creditorId, group))
            throw new IllegalArgumentException("Creditor doesn't belong to the group.");
        groupExpense.setCreditor(creditor);
        BigDecimal amount = expenseDto.amount;
        groupExpense.setAmount(amount);
        LinkedList<PersonGroupExpense> debtors = new LinkedList<>();
        for (ExpenseParticipantCreateDto participant : expenseDto.debtors) {
            if (!personGroupRepository.existsByIdAndGroup(participant.debtorId, group))
                throw new IllegalArgumentException("One of the debtors doesn't belong to the group.");
            PersonGroup person = personGroupRepository.findById(participant.debtorId)
                    .orElseThrow(() -> new EntityNotFoundException("Debtor has not been found"));
            PersonGroupExpense personExpense = new PersonGroupExpense(participant.weight, person, groupExpense);
            debtors.add(personExpense);
        }
        groupExpense.setPersonGroupExpenses(debtors);
        group.addExpense(groupExpense);
        groupExpenseRepository.save(groupExpense);
        notificationService.sendNotificationToUserAccounts(
                "New expense in " + group.getName(),
                creditor.getPerson().getName() + " paid " + groupExpense.getAmount() + " "
                        + groupExpense.getCurrency().getAbbreviation() + " in group " + group.getName() +
                        ". Don't wait too long to pay them back!",
                debtors.stream()
                        .filter(d -> !d.getDebtor().getPerson().getId().equals(issuer.getId()))
                        .map(d -> d.getDebtor().getPerson().getUserAccount())
                        .collect(Collectors.toList())
        );
    }

    public void deleteExpense(Long expenseId, Person issuer) {
        GroupExpense expense = groupExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("There is no expense with the given id"));
        Group group = expense.getGroup();
        groupExpenseRepository.delete(expense);
        group.deleteExpense(expense);
        groupRepository.save(group);
        notificationService.sendNotificationToUserAccount(
                "Expense deleted",
                issuer.getName() + " has just removed expense '" + expense.getTitle() + "' in group "
                        + group.getName() + " where You were a creditor.",
                expense.getCreditor().getPerson().getUserAccount()
        );
    }

    public void addGroupMember(Long groupId, Long personId) {
        Group group = this.getGroupById(groupId);
        Person person = personService.getPersonById(personId);
        PersonGroup member = new PersonGroup(person, group);
        personGroupRepository.save(member);
        group.addMember(member);
        groupRepository.save(group);
        notificationService.sendNotificationToUserAccount(
                "Welcome to " + group.getName() + "!",
                "You have just been added to " + group.getName() + ". Have fun sharing expenses!",
                member.getPerson().getUserAccount()
        );
    }

    public void sendDebtNotification(Person issuer, Long groupId) {
        PersonGroup membership = personGroupRepository.findByPersonAndGroup_Id(issuer, groupId)
                .orElseThrow(EntityNotFoundException::new);
        StringBuilder owings = new StringBuilder();
        List<UserAccount> notificationRecipients = new ArrayList<>();
        boolean first = true;
        for (Map.Entry<Currency, BigDecimal> balance : membership.getBalances().entrySet()) {
            if (balance.getValue().compareTo(BigDecimal.ZERO) > 0) {
                if (!first)
                    owings.append(", ");
                owings.append(balance.getValue()).append(" in ").append(balance.getKey().getAbbreviation());
                first = false;
                notificationRecipients.addAll(membership.getGroup().getMembers()
                        .stream()
                        .filter(personGroup -> personGroup.getBalances().containsKey(balance.getKey()))
                        .filter(personGroup -> personGroup.getBalances().get(balance.getKey()).compareTo(BigDecimal.ZERO) < 0)
                        .filter(personGroup -> !personGroup.getId().equals(membership.getId()))
                        .map(personGroup -> personGroup.getPerson().getUserAccount())
                        .collect(Collectors.toList())
                );
            }
        }
        if (owings.length() == 0) {
            throw new IllegalStateException("Cannot request debt reminder when owed no money.");
        }

        notificationService.sendNotificationToUserAccounts("Debt reminder",
                "You have pending debts in group " + membership.getGroup().getName() + ". "
                        + membership.getPerson().getName() + " is owed " + owings + " and wants to settle up!",
                notificationRecipients);
    }

    public ScheduledGroupExpense scheduleGroupExpense(Long groupId, ScheduledGroupExpenseCreateDto createDto) {
        ScheduledGroupExpense scheduledGroupExpense = new ScheduledGroupExpense();
        Group group = getGroupById(groupId);
        Currency currency = currencyRepository.findCurrencyByAbbreviation(createDto.currency)
                .orElseThrow(EntityNotFoundException::new);
        scheduledGroupExpense.setCurrency(currency);
        scheduledGroupExpense.setGroup(group);
        PersonGroup creditor = personGroupRepository.findById(createDto.creditorId)
                .orElseThrow(() -> new EntityNotFoundException("Creditor has not been found"));
        if (!personGroupRepository.existsByIdAndGroup(createDto.creditorId, group))
            throw new IllegalArgumentException("Creditor doesn't belong to the group.");
        scheduledGroupExpense.setCreditor(creditor);
        BigDecimal amount = createDto.amount;
        scheduledGroupExpense.setAmount(amount);
        LinkedList<ScheduledPersonGroupExpense> debtors = new LinkedList<>();
        for (ScheduledExpenseParticipantCreateDto participant : createDto.debtors) {
            if (!personGroupRepository.existsByIdAndGroup(participant.debtorId, group))
                throw new IllegalArgumentException("One of the debtors doesn't belong to the group.");
            PersonGroup person = personGroupRepository.findById(participant.debtorId)
                    .orElseThrow(() -> new EntityNotFoundException("Debtor has not been found"));
            ScheduledPersonGroupExpense personExpense = ScheduledPersonGroupExpense.builder()
                    .weight(participant.weight)
                    .debtor(person)
                    .scheduledGroupExpense(scheduledGroupExpense)
                    .build();
            debtors.add(personExpense);
        }

        FrequencyUnit frequencyUnit = FrequencyUnit.valueOf(createDto.schedule.frequencyUnit);

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
        scheduledGroupExpense.setTitle(createDto.title);
        scheduledGroupExpense.setSchedule(schedule);
        scheduledGroupExpense.setScheduledPersonGroupExpenses(debtors);
        return scheduledGroupExpenseRepository.save(scheduledGroupExpense);
    }

    public void deleteScheduledGroupExpense(Long groupId, Long expenseId, Person issuer) throws IllegalAccessException {
        // Check if expense related to group
        Group group = getGroupById(groupId);
        ScheduledGroupExpense expense = scheduledGroupExpenseRepository.findById(expenseId)
                .orElseThrow(EntityNotFoundException::new);
        if (!expense.getGroup().getId().equals(group.getId()))
            throw new IllegalAccessException("Scheduled expense is not related to this group.");

        // Check if issuer is expense creditor
        if (!issuer.getId().equals(expense.getCreditor().getPerson().getId()))
            throw new IllegalAccessException("Must be a creditor of an expense in order to remove it.");

        scheduledGroupExpenseRepository.delete(expense);
    }

}
