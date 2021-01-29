package com.splitthebill.server.service;

import com.splitthebill.server.model.user.Friendship;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.repository.FriendshipRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    @NonNull
    private FriendshipRepository friendshipRepository;

    @NonNull
    private NotificationService notificationService;

    public Friendship getFriendshipById(Long id){
        return friendshipRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Friendship> getAllConfirmedFriendshipsForPerson(Person person){
        List<Friendship> requestedFriendships = friendshipRepository.getAllByPerson1AndConfirmed(person, true);
        List<Friendship> confirmedFriendships = friendshipRepository.getAllByPerson2AndConfirmed(person, true);
        return requestedFriendships.stream()
                .takeWhile(friendship -> confirmedFriendships.stream().anyMatch(friendship::relatesToSamePeopleAs))
                .collect(Collectors.toList());
    }

    public List<Friendship> getAllPendingFriendshipForPerson(Person person) {
        List<Friendship> requestedFriendships = friendshipRepository.getAllByPerson1AndConfirmed(person, true);
        List<Friendship> confirmedFriendships = friendshipRepository.getAllByPerson2AndConfirmed(person, false);
        return requestedFriendships.stream()
                .takeWhile(friendship -> confirmedFriendships.stream().anyMatch(friendship::relatesToSamePeopleAs))
                .collect(Collectors.toList());
    }

    public List<Friendship> getAllFriendshipRequestsForPerson(Person person) {
        List<Friendship> receivedFriendships = friendshipRepository.getAllByPerson1AndConfirmed(person, false);
        List<Friendship> requestedFriendships = friendshipRepository.getAllByPerson2AndConfirmed(person, true);
        return receivedFriendships.stream()
                .takeWhile(friendship -> requestedFriendships.stream().anyMatch(friendship::relatesToSamePeopleAs))
                .collect(Collectors.toList());
    }

    public Friendship sendFriendshipRequest(Person issuer, Person receiver){
        if(issuer == null || receiver == null)
            throw new EntityNotFoundException();
        if(issuer.equals(receiver))
            throw new DataIntegrityViolationException("Friendship members cannot be the same person.");
        boolean isDuplicate = issuer.getFriendships()
                .stream().anyMatch(friendship -> receiver.getFriendships()
                        .stream().anyMatch(friendship::relatesToSamePeopleAs));
        if(isDuplicate)
            throw new EntityExistsException("Requested friendship already exists.");
        Friendship issuerToReceiver = new Friendship(issuer, receiver, true);
        Friendship receiverToIssuer = new Friendship(receiver, issuer, false);
        issuerToReceiver = friendshipRepository.save(issuerToReceiver);
        friendshipRepository.save(receiverToIssuer);
        notificationService.sendNotificationToUserAccount(
                "New friendship request",
                "You have a new friendship request from " + issuer.getName(),
                receiver.getUserAccount()
        );
        return issuerToReceiver;
    }

    public Friendship acceptFriendshipRequest(Long id) {
        Friendship acceptedFriendship = friendshipRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        acceptedFriendship.setConfirmed(true);
        acceptedFriendship = friendshipRepository.save(acceptedFriendship);
        notificationService.sendNotificationToUserAccount(
                "Accepted friendship",
                acceptedFriendship.getPerson1().getName() + " has accepted your friendship request. " +
                        "You are friends now!",
                acceptedFriendship.getPerson2().getUserAccount()
        );
        return acceptedFriendship;
    }

    public void breakFriendship(Long id) {
        Friendship friendshipToBreak = friendshipRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Friendship followingFriendship = friendshipRepository
                .getByPerson1AndPerson2AndConfirmedIsTrue(friendshipToBreak.getPerson2(), friendshipToBreak.getPerson1())
                .orElseThrow(EntityExistsException::new);
        friendshipRepository.deleteAll(List.of(friendshipToBreak, followingFriendship));
    }
}
