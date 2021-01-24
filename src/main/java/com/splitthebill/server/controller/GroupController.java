package com.splitthebill.server.controller;

import com.splitthebill.server.dto.group.GroupCreateDto;
import com.splitthebill.server.dto.group.GroupExpenseCreateDto;
import com.splitthebill.server.dto.group.GroupReadDto;
import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.GroupService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/groups")
public class GroupController {

    @NonNull
    private final GroupService groupService;

    @NonNull
    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<?> createGroup(@Valid @RequestBody GroupCreateDto group) {
        try {
            Group createdGroup = groupService.createGroup(group);
            return ResponseEntity.ok(new GroupReadDto(createdGroup));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable Long id, Authentication authentication) {
        try {
            Person person = jwtUtils.getUserAccountFromAuthentication(authentication).getPerson();
            if (!person.isMemberOfGroup(id))
                throw new IllegalAccessException("Must be a member of the group.");
            Group createdGroup = groupService.getGroupById(id);
            return ResponseEntity.ok(new GroupReadDto(createdGroup));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/expenses")
    public ResponseEntity<?> createExpense(@Valid @RequestBody GroupExpenseCreateDto expenseDto,
                                           Authentication authentication) {
        try {
            Person person = jwtUtils.getUserAccountFromAuthentication(authentication).getPerson();
            if (!person.isMemberOfGroup(expenseDto.groupId))
                throw new IllegalAccessException("Must be a member of the group.");
            groupService.addExpense(expenseDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
