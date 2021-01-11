package com.splitthebill.server.model;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class UserAccountNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isReviewed;

    @ManyToOne
    private Notification notification;
}
