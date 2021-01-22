package com.splitthebill.server.model.user;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private Date created;

    @OneToMany(mappedBy = "notification")
    private List<UserAccountNotification> userAccountNotifications;

}
