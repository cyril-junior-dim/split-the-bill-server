package com.splitthebill.server.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UserAccountCreateDto {

    @Pattern(regexp = "[a-z0-9]{4,15}", message = "Should contain only lowercase letters or numbers and be 4-15 characters long.")
    public String username;

    @NotBlank
    public String password;

    @Pattern(regexp = "[a-z0-9.]+@[a-z0-9][a-z0-9.]*\\.[a-z]{2,3}", message = "The email address is incorrect.")
    public String email;

}
