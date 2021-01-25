package com.splitthebill.server.controller;

import com.splitthebill.server.dto.friendship.FriendshipCollectionReadDto;
import com.splitthebill.server.dto.friendship.FriendshipCreateDto;
import com.splitthebill.server.dto.friendship.FriendshipReadDto;
import com.splitthebill.server.model.user.Friendship;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.FriendshipService;
import com.splitthebill.server.service.UserAccountService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friendships")
public class FriendshipController {

    @NonNull
    private final FriendshipService friendshipService;
    @NonNull
    private final UserAccountService userAccountService;
    @NonNull
    JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> getAllFriendships(Authentication authentication) {
        Person person;
        try {
            person = jwtUtils.getPersonFromAuthentication(authentication);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        List<FriendshipReadDto> confirmedFriendships =
                friendshipService.getAllConfirmedFriendshipsForPerson(person).stream()
                        .map(this::assembleDto)
                        .collect(Collectors.toList());
        List<FriendshipReadDto> pendingFriendships =
                friendshipService.getAllPendingFriendshipForPerson(person).stream()
                        .map(this::assembleDto)
                        .collect(Collectors.toList());
        List<FriendshipReadDto> receivedRequests =
                friendshipService.getAllFriendshipRequestsForPerson(person).stream()
                        .map(this::assembleDto)
                        .collect(Collectors.toList());
        FriendshipCollectionReadDto collectionReadDto = new FriendshipCollectionReadDto();
        collectionReadDto.friendships.put("confirmed", confirmedFriendships);
        collectionReadDto.friendships.put("pending", pendingFriendships);
        collectionReadDto.friendships.put("receivedRequests", receivedRequests);
        return ResponseEntity.ok(collectionReadDto);
    }

    @PostMapping
    public ResponseEntity<?> sendFriendshipRequest(@RequestBody FriendshipCreateDto friendshipCreateDto,
                                                   Authentication authentication) {
        try {
            Person requestingPerson = jwtUtils.getPersonFromAuthentication(authentication);
            Person targetPerson = userAccountService
                    .getUserAccountByIdentifierAttribute(friendshipCreateDto.identifierAttribute).getPerson();
            if (targetPerson == null)
                throw new EntityNotFoundException();

            Friendship requestedFriendship =
                    friendshipService.sendFriendshipRequest(requestingPerson, targetPerson);
            return ResponseEntity.ok(assembleDto(requestedFriendship));
        } catch (EntityNotFoundException entityNotFoundException) {
            return ResponseEntity.notFound().build();
        } catch (DataIntegrityViolationException | EntityExistsException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptFriendshipRequest(@PathVariable Long id, Authentication authentication) {
        Person requestIssuer;
        try {
            requestIssuer = jwtUtils.getPersonFromAuthentication(authentication);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        Friendship friendshipToConfirm = friendshipService.getFriendshipById(id);
        if (friendshipToConfirm.getPerson1().equals(requestIssuer) && !friendshipToConfirm.isConfirmed()) {
            friendshipToConfirm = friendshipService.acceptFriendshipRequest(id);
            return ResponseEntity.ok(assembleDto(friendshipToConfirm));
        }
        return ResponseEntity.badRequest().body("Must be a receiver of a friendship request.");
    }

    @DeleteMapping("/{id}/break")
    public ResponseEntity<?> breakFriendship(@PathVariable Long id, Authentication authentication) {
        Person requestIssuer;
        try {
            requestIssuer = jwtUtils.getPersonFromAuthentication(authentication);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        Friendship friendshipToConfirm = friendshipService.getFriendshipById(id);
        if (requestIssuer.equals(friendshipToConfirm.getPerson1())) {
            friendshipService.breakFriendship(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().body("Must be an initiator of a friendship.");
    }

    private FriendshipReadDto assembleDto(Friendship friendship) {
        return new FriendshipReadDto(friendship.getId(), friendship.getPerson2().getName());
    }
}
