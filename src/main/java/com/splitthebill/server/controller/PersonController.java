package com.splitthebill.server.controller;

import com.splitthebill.server.dto.PersonCreateDto;
import com.splitthebill.server.dto.PersonReadDto;
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
import org.springframework.web.server.MethodNotAllowedException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/people")
@RequiredArgsConstructor
public class PersonController {

    @NonNull
    private final PersonService personService;
    @NonNull
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> getPerson(Authentication authentication) {
        Person person;
        try {
            person = jwtUtils.getPersonFromAuthentication(authentication);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(assembleLinks(authentication, person));
    }

    @PostMapping
    public ResponseEntity<?> createPerson(@RequestBody PersonCreateDto personCreateDto, Authentication authentication) {
        try {
            UserAccount userAccount = jwtUtils.getUserAccountFromAuthentication(authentication);
            if (userAccount.getPerson() != null)
                throw new IllegalAccessException("There's already a person assigned.");
            Person createdPerson = personService.createPerson(userAccount, personCreateDto);
            PersonReadDto linkedPerson = assembleLinks(authentication, createdPerson);
            return ResponseEntity.created(linkedPerson.getLink("self").get().toUri())
                    .body(linkedPerson);
        } catch (EntityNotFoundException | IllegalAccessException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping
    public ResponseEntity<?> updatePerson(@RequestBody PersonCreateDto personCreateDto, Authentication authentication) {
        try {
            Person existingPerson = jwtUtils.getPersonFromAuthentication(authentication);
            Person updatedPerson = personService.updatePerson(existingPerson, personCreateDto);
            return ResponseEntity.ok(assembleLinks(authentication, updatedPerson));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private PersonReadDto assembleLinks(Authentication authentication, Person person) {
        PersonReadDto personReadDto = new PersonReadDto(person.getId(), person.getName(), person.getBalancesM());
        Link selfLink = linkTo(methodOn(PersonController.class).getPerson(authentication)).withSelfRel();
        Link userAccountLink = linkTo(methodOn(UserAccountController.class).getUserAccount(authentication)).withRel("userAccount");
        Link friendshipsLink = linkTo(methodOn(FriendshipController.class).getAllFriendships(authentication)).withRel("friendships");
        //TODO missing links - personGroups, ownExpenses,
        // groupExpenses, personGroupExpenses, scheduledPersonGroupExpenses

        return personReadDto.add(List.of(selfLink, userAccountLink, friendshipsLink));
    }
}
