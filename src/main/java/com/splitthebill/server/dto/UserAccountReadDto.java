package com.splitthebill.server.dto;

import lombok.AllArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
public class UserAccountReadDto extends RepresentationModel<UserAccountReadDto> {

    public String email;

}
