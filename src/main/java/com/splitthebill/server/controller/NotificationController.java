package com.splitthebill.server.controller;

import com.splitthebill.server.dto.NotificationReadDto;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.model.user.UserAccountNotification;
import com.splitthebill.server.security.JwtUtils;
import com.splitthebill.server.service.NotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    @NonNull
    private final NotificationService notificationService;

    @NonNull
    private final JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> getAllNotifications(Authentication authentication){
        UserAccount userAccount;
        try{
            userAccount = jwtUtils.getUserAccountFromAuthentication(authentication);
        } catch (EntityNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        List<NotificationReadDto> notifications = notificationService.getUserAccountNotifications(userAccount)
                .stream()
                .map(this::assembleDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{id}/markReviewed")
    public ResponseEntity<?> markNotificationReviewed(@PathVariable Long id, Authentication authentication){
        try{
            UserAccount userAccount = jwtUtils.getUserAccountFromAuthentication(authentication);
            UserAccountNotification notification = notificationService.getUserAccountNotificationById(id);
            if(notification.getUserAccount().equals(userAccount)){
                notification = notificationService.markUserAccountNotificationReviewed(notification);
                return ResponseEntity.ok(assembleDto(notification));
            }
            return ResponseEntity.badRequest().body("Notification is not assigned to a given account.");
        } catch (EntityNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private NotificationReadDto assembleDto(UserAccountNotification userAccountNotification){
        return new NotificationReadDto(userAccountNotification.getId(),
                userAccountNotification.getNotification().getTitle(),
                userAccountNotification.getNotification().getDescription(),
                userAccountNotification.isReviewed());
    }
}
