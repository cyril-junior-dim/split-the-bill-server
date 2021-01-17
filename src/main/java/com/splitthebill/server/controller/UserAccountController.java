package com.splitthebill.server.controller;

import com.splitthebill.server.dto.BasicUserAccountCreateDto;
import com.splitthebill.server.service.BasicUserAccountService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/userAccounts")
@RequiredArgsConstructor
public class UserAccountController {

    @NonNull
    BasicUserAccountService basicUserAccountService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getUserAccount(@PathVariable Long id) {
        //TODO add links
        try {
            return ResponseEntity.ok().body(basicUserAccountService.getUserAccountById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createUserAccount(@Valid @RequestBody BasicUserAccountCreateDto account) {
        try {
            return ResponseEntity.ok().body(basicUserAccountService.createUserAccount(account));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
