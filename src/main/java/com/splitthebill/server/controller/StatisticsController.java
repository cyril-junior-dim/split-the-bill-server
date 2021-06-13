package com.splitthebill.server.controller;

import com.splitthebill.server.dto.group.GroupExpenseReadDto;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.StatisticsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    @NonNull
    private final JwtUtils jwtUtils;
    @NonNull
    private final StatisticsService statisticsService;

    @GetMapping("/allExpenses")
    public ResponseEntity<?> getAllPersonExpenses(Authentication authentication) {
        try {
            Person person = jwtUtils.getPersonFromAuthentication(authentication);
            Map<String, Map<LocalDate, List<GroupExpenseReadDto>>> personExpenses = statisticsService.getAllPersonExpenses(person);
            return ResponseEntity.ok(personExpenses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
