package com.splitthebill.server.model.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    //TODO store password in the right way
    private String password;

    private String email;

    @CreationTimestamp
    private LocalDateTime created;

    @OneToOne
    private Person person;

    @OneToMany(mappedBy = "userAccount")
    private List<UserAccountNotification> userAccountNotifications;

}
