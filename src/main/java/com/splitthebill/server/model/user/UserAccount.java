package com.splitthebill.server.model.user;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String email;

    private Date created;

    @OneToMany(mappedBy = "userAccount")
    private List<UserAccountNotification> userAccountNotifications;

}
