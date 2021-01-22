package com.splitthebill.server.dto;

import javax.validation.constraints.Pattern;

public class ThirdPartyUserAccountCreateDto {

    @Pattern(regexp = "[a-z0-9.]+@[a-z0-9][a-z0-9.]*\\.[a-z]{2,3}", message = "The email address is incorrect.")
    public String email;

    public String name;

    public String provider;
}
