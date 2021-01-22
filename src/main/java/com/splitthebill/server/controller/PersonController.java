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

import java.util.List;

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
        return ResponseEntity.ok(assembleLinks(authentication, userAccount.getPerson()));
    }

    @PostMapping
    public ResponseEntity<?> createPerson(@RequestBody PersonCreateDto personCreateDto, Authentication authentication){
        UserAccount userAccount = jwtUtils.getUserAccountFromAuthentication(authentication);
        Person createdPerson = personService.createPerson(userAccount, personCreateDto);
        Person linkedPerson = assembleLinks(authentication, createdPerson);
        return ResponseEntity.created(linkedPerson.getLink("self").get().toUri())
                .body(createdPerson);
    }

    @PatchMapping
    public ResponseEntity<?> updatePerson(@RequestBody PersonCreateDto personCreateDto, Authentication authentication){
        UserAccount userAccount = jwtUtils.getUserAccountFromAuthentication(authentication);
        Person updatedPerson = personService.updatePerson(userAccount.getPerson(), personCreateDto);
        return ResponseEntity.ok(assembleLinks(authentication, updatedPerson));
    }

    private Person assembleLinks(Authentication authentication, Person person) {
        Link selfLink = linkTo(methodOn(PersonController.class).getPerson(authentication)).withSelfRel();
        Link userAccountLink = linkTo(methodOn(UserAccountController.class).getUserAccount(authentication)).withRel("userAccount");
        //TODO missing links - friendships, personGroups, ownExpenses,
        // groupExpenses, personGroupExpenses, scheduledPersonGroupExpenses

        return person.add(List.of(selfLink, userAccountLink));
    }
}
