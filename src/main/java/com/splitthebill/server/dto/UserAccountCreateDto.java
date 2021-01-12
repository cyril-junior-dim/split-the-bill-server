package com.splitthebill.server.dto;

import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class UserAccountCreateDto {

    @Pattern(regexp = "[a-z0-9]{4,15}", message = "Should contain only lowercase letters or numbers and be 4-15 characters long.")
    private String username;

    private String password;

    @Pattern(regexp = "[a-z0-9.]+@[a-z0-9.]+\\.[a-z]{2,3}", message = "The email address is incorrect.")
    private String email;

}
