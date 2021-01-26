package com.splitthebill.server.service;

import com.splitthebill.server.model.user.Notification;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.model.user.UserAccountNotification;
import com.splitthebill.server.repository.UserAccountNotificationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    @NonNull
    private final UserAccountNotificationRepository userAccountNotificationRepository;

    public List<UserAccountNotification> getUserAccountNotifications(UserAccount userAccount) {
        return userAccountNotificationRepository.findAllByUserAccount(userAccount);
    }

    public UserAccountNotification getUserAccountNotificationById(Long id) {
        return userAccountNotificationRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public UserAccountNotification markUserAccountNotificationReviewed(UserAccountNotification notification) {
        notification.markReviewed();
        return userAccountNotificationRepository.save(notification);
    }

    public UserAccountNotification sendNotificationToUserAccount(String title, String description,
                                                                 UserAccount userAccount) {
        Notification notification = new Notification(title, description);
        UserAccountNotification userAccountNotification = new UserAccountNotification(notification, userAccount);
        return userAccountNotificationRepository.save(userAccountNotification);
    }

    public Iterable<UserAccountNotification> sendNotificationToUserAccounts(String title, String description,
                                                                            List<UserAccount> recipients) {
        Notification notification = new Notification(title, description);
        List<UserAccountNotification> notificationsToBeSend = recipients
                .stream()
                .map(recipient -> new UserAccountNotification(notification, recipient))
                .collect(Collectors.toList());
        return userAccountNotificationRepository.saveAll(notificationsToBeSend);
    }
}
