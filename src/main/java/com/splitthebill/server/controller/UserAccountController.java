package com.splitthebill.server.controller;

import com.splitthebill.server.dto.UserAccountCreateDto;
import com.splitthebill.server.service.UserAccountService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RepositoryRestController
@RequestMapping(path = "/userAccounts")
@RequiredArgsConstructor
public class UserAccountController {

    @NonNull
    UserAccountService userAccountService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getUserAccount(@PathVariable Long id) {
        //TODO add links
        try {
            return ResponseEntity.ok().body(userAccountService.getUserAccountById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createUserAccount(UserAccountCreateDto account) {
        try {
            return ResponseEntity.ok().body(userAccountService.createUserAccount(account));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
