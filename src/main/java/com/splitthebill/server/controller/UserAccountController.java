package com.splitthebill.server.controller;

import com.splitthebill.server.dto.UserAccountCreateDto;
import com.splitthebill.server.service.UserAccountService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<?> createUserAccount(@Valid @RequestBody UserAccountCreateDto account) {
        try {
            return ResponseEntity.ok().body(userAccountService.createUserAccount(account));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
