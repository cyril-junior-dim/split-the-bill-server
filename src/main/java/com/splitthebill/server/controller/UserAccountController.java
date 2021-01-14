package com.splitthebill.server.controller;

import com.splitthebill.server.dto.UserAccountCreateDto;
import com.splitthebill.server.service.UserAccountService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
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
    public ResponseEntity<?> createUserAccount(@Valid @RequestBody UserAccountCreateDto account,
                                               BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                //TODO make it work.
                return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
            }
            return ResponseEntity.ok().body(userAccountService.createUserAccount(account));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
