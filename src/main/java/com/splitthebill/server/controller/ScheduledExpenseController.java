package com.splitthebill.server.controller;

import com.splitthebill.server.dto.expense.scheduled.ScheduledExpenseReadDto;
import com.splitthebill.server.dto.expense.scheduled.own.ScheduledOwnExpenseCreateDto;
import com.splitthebill.server.dto.expense.scheduled.own.ScheduledOwnExpenseReadDto;
import com.splitthebill.server.model.expense.scheduled.group.ScheduledGroupExpense;
import com.splitthebill.server.model.expense.scheduled.own.ScheduledOwnExpense;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.ScheduledExpenseService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/scheduledExpenses")
@RequiredArgsConstructor
public class ScheduledExpenseController {

    @NonNull
    private final ScheduledExpenseService scheduledExpenseService;
    @NonNull
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> getAllScheduledExpenses(Authentication authentication) {
        try {
            Person person = jwtUtils.getPersonFromAuthentication(authentication);
            List<ScheduledGroupExpense> scheduledGroupExpenses =
                    scheduledExpenseService.getAllScheduledGroupExpenses(person);
            List<ScheduledOwnExpense> scheduledOwnExpenses =
                    scheduledExpenseService.getAllScheduledOwnExpenses(person);
            return ResponseEntity.ok(new ScheduledExpenseReadDto(scheduledGroupExpenses, scheduledOwnExpenses));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> scheduleOwnExpense(@RequestBody ScheduledOwnExpenseCreateDto createDto,
                                                Authentication authentication) {
        try {
            Person person = jwtUtils.getPersonFromAuthentication(authentication);
            ScheduledOwnExpense scheduledOwnExpense = scheduledExpenseService.scheduledOwnExpense(person, createDto);
            return ResponseEntity.ok(new ScheduledOwnExpenseReadDto(scheduledOwnExpense));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping(path = "/{expenseId}")
    public ResponseEntity<?> deleteScheduledExpense(@PathVariable Long expenseId, Authentication authentication) {
        try {
            Person issuer = jwtUtils.getPersonFromAuthentication(authentication);
            scheduledExpenseService.deleteScheduledOwnExpense(expenseId, issuer);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException | IllegalAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
