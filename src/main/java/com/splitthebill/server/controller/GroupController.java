package com.splitthebill.server.controller;

import com.splitthebill.server.dto.group.GroupCreateDto;
import com.splitthebill.server.dto.group.GroupExpenseCreateDto;
import com.splitthebill.server.dto.group.GroupReadDto;
import com.splitthebill.server.model.Group;
import com.splitthebill.server.service.GroupService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/groups")
public class GroupController {

    @NonNull
    private final GroupService groupService;

    //TODO authentication

    @PostMapping
    public ResponseEntity<?> createGroup(@Valid @RequestBody GroupCreateDto group,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            Group createdGroup = groupService.createGroup(group);
            return ResponseEntity.ok(new GroupReadDto(createdGroup));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable Long id) {
        try {
            Group createdGroup = groupService.getGroupById(id);
            return ResponseEntity.ok(new GroupReadDto(createdGroup));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(path = "/expenses")
    public ResponseEntity<?> createExpense(@Valid @RequestBody GroupExpenseCreateDto expenseDto) {
        try {
            groupService.addExpense(expenseDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
