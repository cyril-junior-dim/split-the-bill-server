package com.splitthebill.server.controller;

import com.splitthebill.server.dto.UserAccountReadDto;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.security.JwtUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/userAccounts")
@RequiredArgsConstructor
public class UserAccountController {

    @NonNull
    JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> getUserAccount(Authentication authentication) {
        UserAccount userAccount = jwtUtils.getUserAccountFromAuthentication(authentication);
        return ResponseEntity.ok(assembleLinks(authentication, userAccount));
    }

    private UserAccountReadDto assembleLinks(Authentication authentication, UserAccount userAccount) {
        UserAccountReadDto userAccountReadDto = new UserAccountReadDto(userAccount.getEmail());
        Link self = linkTo(methodOn(UserAccountController.class).getUserAccount(authentication)).withSelfRel();
        Link person = linkTo(methodOn(PersonController.class).getPerson(authentication)).withRel("person");
        //TODO missing links - notifications

        return userAccountReadDto.add(List.of(self, person));
    }
}
