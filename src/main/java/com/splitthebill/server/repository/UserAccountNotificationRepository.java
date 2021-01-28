package com.splitthebill.server.repository;

import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.model.user.UserAccountNotification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAccountNotificationRepository extends CrudRepository<UserAccountNotification, Long> {

    List<UserAccountNotification> findAllByUserAccount(UserAccount userAccount);

}
