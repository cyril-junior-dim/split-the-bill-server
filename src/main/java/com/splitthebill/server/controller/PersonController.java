package com.splitthebill.server.controller;

import com.splitthebill.server.dto.PersonCreateDto;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.PersonService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
public class PersonController {

    @NonNull
    private JwtUtils jwtUtils;

    @NonNull
    private final PersonService personService;

    @GetMapping
    public ResponseEntity<?> getPerson(Authentication authentication){
        UserAccount userAccount = jwtUtils.getUserAccountFromAuthentication(authentication);
        return ResponseEntity.ok(userAccount.getPerson());
    }

    @PostMapping
    public ResponseEntity<?> createPerson(@RequestBody PersonCreateDto personCreateDto, Authentication authentication){
        UserAccount userAccount = jwtUtils.getUserAccountFromAuthentication(authentication);
        Person createdPerson = personService.createPerson(userAccount, personCreateDto);
        Link selfLink = linkTo(methodOn(PersonController.class).getPerson(authentication)).withSelfRel();
        createdPerson.add(selfLink);
        return ResponseEntity.created(selfLink.toUri()).body(createdPerson);
    }

    @PatchMapping
    public ResponseEntity<?> updatePerson(@RequestBody PersonCreateDto personCreateDto, Authentication authentication){
        UserAccount userAccount = jwtUtils.getUserAccountFromAuthentication(authentication);
        Person updatedPerson = personService.updatePerson(userAccount.getPerson(), personCreateDto);
        Link selfLink = linkTo(methodOn(PersonController.class).getPerson(authentication)).withSelfRel();
        updatedPerson.add(selfLink);
        return ResponseEntity.created(selfLink.toUri()).body(updatedPerson);
    }
}
