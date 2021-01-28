package com.splitthebill.server.controller;

import com.splitthebill.server.dto.expense.scheduled.group.ScheduledGroupExpenseCreateDto;
import com.splitthebill.server.dto.expense.scheduled.group.ScheduledGroupExpenseReadDto;
import com.splitthebill.server.dto.group.GroupCreateDto;
import com.splitthebill.server.dto.group.GroupExpenseCreateDto;
import com.splitthebill.server.dto.group.GroupReadDto;
import com.splitthebill.server.dto.group.PersonGroupReadDto;
import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.expense.scheduled.group.ScheduledGroupExpense;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.PersonGroup;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.GroupService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/groups")
public class GroupController {

    @NonNull
    private final GroupService groupService;

    @NonNull
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> getUserGroups(Authentication authentication) {
        try {
            Person person = jwtUtils.getPersonFromAuthentication(authentication);
            return ResponseEntity.ok(person.getPersonGroups().stream()
                    .map(PersonGroupReadDto::new));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createGroup(@Valid @RequestBody GroupCreateDto group) {
        try {
            Group createdGroup = groupService.createGroup(group);
            return ResponseEntity.ok(new GroupReadDto(createdGroup));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping(path = "/{groupId}/expenses")
    public ResponseEntity<?> deleteExpense(@PathVariable Long groupId, @RequestParam Long expenseId,
                                           Authentication authentication) {
        try {
            groupService.getGroupById(groupId);
            Person person = jwtUtils.getPersonFromAuthentication(authentication);
            if (!person.isMemberOfGroup(groupId))
                throw new IllegalAccessException("Must be a member of the group");
            groupService.deleteExpense(expenseId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | IllegalAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(path = "/{groupId}")
    public ResponseEntity<?> getGroupById(@PathVariable Long groupId, Authentication authentication) {
        try {
            Group createdGroup = groupService.getGroupById(groupId);
            Person person = jwtUtils.getPersonFromAuthentication(authentication);
            if (!person.isMemberOfGroup(groupId))
                throw new IllegalAccessException("Must be a member of the group.");
            return ResponseEntity.ok(new GroupReadDto(createdGroup));
        } catch (EntityNotFoundException | IllegalAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping(path = "/{groupId}/add")
    public ResponseEntity<?> addGroupMember(@PathVariable Long groupId, @RequestParam Long personId,
                                            Authentication authentication) {
        try {
            Person person = jwtUtils.getPersonFromAuthentication(authentication);
            if (!person.isMemberOfGroup(groupId))
                throw new IllegalAccessException("Must be a member of the group.");
            groupService.addGroupMember(groupId, personId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | IllegalAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/{groupId}/expenses")
    public ResponseEntity<?> createExpense(@PathVariable Long groupId,
                                           @Valid @RequestBody GroupExpenseCreateDto expenseDto,
                                           Authentication authentication) {
        try {
            Person person = jwtUtils.getPersonFromAuthentication(authentication);
            if (!person.isMemberOfGroup(groupId))
                throw new IllegalAccessException("Must be a member of the group.");
            groupService.addExpense(groupId, expenseDto);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | IllegalAccessException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(path = "/{groupId}/settleUpExpenses")
    public ResponseEntity<?> getSettleUpExpenses(@PathVariable Long groupId,
                                                 Authentication authentication) {
        try {
            Person person = jwtUtils.getPersonFromAuthentication(authentication);
            if (!person.isMemberOfGroup(groupId))
                throw new IllegalAccessException("Must be a member of the group.");
            PersonGroup membership = person.getPersonGroups()
                    .stream()
                    .filter(personGroup -> personGroup.getGroup().getId().equals(groupId))
                    .findFirst()
                    .get();
            return ResponseEntity.ok(membership.getSettleUpExpenses().stream().map(GroupExpenseCreateDto::new));
        } catch (EntityNotFoundException | IllegalAccessException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/{groupId}/sendDebtReminder")
    public ResponseEntity<?> sendDebtReminder(@PathVariable Long groupId, Authentication authentication) {
        try {
            Person issuer = jwtUtils.getPersonFromAuthentication(authentication);
            groupService.sendDebtNotification(issuer, groupId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/{groupId}/scheduledExpenses")
    public ResponseEntity<?> scheduleGroupExpense(@PathVariable Long groupId,
                                                  @Valid @RequestBody ScheduledGroupExpenseCreateDto createDto) {
        try {
            ScheduledGroupExpense scheduledGroupExpense = groupService.scheduleGroupExpense(groupId, createDto);
            return ResponseEntity.ok(new ScheduledGroupExpenseReadDto(scheduledGroupExpense));
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping(path = "/{groupId}/scheduledExpenses/{expenseId}")
    public ResponseEntity<?> deleteScheduledExpense(@PathVariable Long groupId, @PathVariable Long expenseId,
                                                    Authentication authentication) {
        try {
            Person issuer = jwtUtils.getPersonFromAuthentication(authentication);
            groupService.deleteScheduledGroupExpense(groupId, expenseId, issuer);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException | IllegalAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
