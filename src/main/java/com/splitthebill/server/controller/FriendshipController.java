package com.splitthebill.server.controller;

import com.splitthebill.server.dto.friendship.FriendshipCreateDto;
import com.splitthebill.server.dto.friendship.FriendshipReadDto;
import com.splitthebill.server.model.user.Friendship;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.FriendshipService;
import com.splitthebill.server.service.UserAccountService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friendships")
public class FriendshipController {

    @NonNull
    private FriendshipService friendshipService;

    @NonNull
    private UserAccountService userAccountService;

    @NonNull
    JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> getAllFriendships(Authentication authentication){
        UserAccount userAccount = jwtUtils.getUserAccountFromAuthentication(authentication);
        List<FriendshipReadDto> confirmedFriendships =
                friendshipService.getAllConfirmedFriendshipsForPerson(userAccount.getPerson()).stream()
                        .map(friendship -> assembleConfirmedFriendshipLinks(authentication, friendship))
                        .collect(Collectors.toList());
        List<FriendshipReadDto> pendingFriendships =
                friendshipService.getAllPendingFriendshipForPerson(userAccount.getPerson()).stream()
                        .map(friendship -> assemblePendingFriendshipLinks(authentication, friendship))
                        .collect(Collectors.toList());
        List<FriendshipReadDto> receivedRequests =
                friendshipService.getAllFriendshipRequestsForPerson(userAccount.getPerson()).stream()
                        .map(friendship -> assembleReceivedFriendshipRequestLinks(authentication, friendship))
                        .collect(Collectors.toList());
        Map<String, List<FriendshipReadDto>> response = new HashMap<>();
        response.put("confirmed", confirmedFriendships);
        response.put("pending", pendingFriendships);
        response.put("receivedRequests", receivedRequests);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> sendFriendshipRequest(@RequestBody FriendshipCreateDto friendshipCreateDto,
                                                   Authentication authentication) {
        UserAccount userAccount = jwtUtils.getUserAccountFromAuthentication(authentication);
        // Try to obtain target user using identifier attribute.
        UserAccount targetUserAccount = userAccountService
                .getUserAccountByIdentifierAttribute(friendshipCreateDto.identifierAttribute);
        Friendship requestedFriendship;
        try {
            requestedFriendship =
                    friendshipService.sendFriendshipRequest(userAccount.getPerson(), targetUserAccount.getPerson());
        }catch (EntityNotFoundException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(assemblePendingFriendshipLinks(authentication, requestedFriendship));
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> acceptFriendshipRequest(@PathVariable Long id, Authentication authentication) {
        UserAccount userAccount = jwtUtils.getUserAccountFromAuthentication(authentication);
        Friendship friendshipToConfirm = friendshipService.getFriendshipById(id);
        if(friendshipToConfirm.getPerson1().equals(userAccount.getPerson())){
            friendshipToConfirm = friendshipService.acceptFriendshipRequest(id);
            return ResponseEntity.ok(assembleConfirmedFriendshipLinks(authentication, friendshipToConfirm));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> breakFriendship(@PathVariable Long id, Authentication authentication){
        UserAccount userAccount = jwtUtils.getUserAccountFromAuthentication(authentication);
        Friendship friendshipToConfirm = friendshipService.getFriendshipById(id);
        if(userAccount.getPerson().equals(friendshipToConfirm.getPerson1())) {
            friendshipService.breakFriendship(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private FriendshipReadDto assembleConfirmedFriendshipLinks(Authentication authentication,
                                                               Friendship friendship){
        FriendshipReadDto friendshipReadDto = new FriendshipReadDto(friendship.getPerson2().getName());
        Link breakFriendshipLink =
                linkTo(methodOn(FriendshipController.class).breakFriendship(friendship.getId(), authentication))
                        .withRel("breakFriendship");
        return friendshipReadDto.add(breakFriendshipLink);
    }

    private FriendshipReadDto assemblePendingFriendshipLinks(Authentication authentication,
                                                             Friendship friendship){
        FriendshipReadDto friendshipReadDto = new FriendshipReadDto(friendship.getPerson2().getName());
        Link breakFriendshipLink =
                linkTo(methodOn(FriendshipController.class).breakFriendship(friendship.getId(), authentication))
                        .withRel("breakFriendship");
        return friendshipReadDto.add(breakFriendshipLink);
    }

    private FriendshipReadDto assembleReceivedFriendshipRequestLinks(Authentication authentication,
                                                                     Friendship friendship){
        FriendshipReadDto friendshipReadDto = new FriendshipReadDto(friendship.getPerson2().getName());
        Link breakFriendshipLink =
                linkTo(methodOn(FriendshipController.class).breakFriendship(friendship.getId(), authentication))
                        .withRel("breakFriendship");
        Link acceptFriendshipLink =
                linkTo(methodOn(FriendshipController.class).acceptFriendshipRequest(friendship.getId(), authentication))
                        .withRel("acceptFriendshipRequest");
        return friendshipReadDto.add(List.of(breakFriendshipLink, acceptFriendshipLink));
    }
}
