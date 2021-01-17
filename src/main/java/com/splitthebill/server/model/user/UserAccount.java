package com.splitthebill.server.model.user;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    String email;

    @CreationTimestamp
    LocalDateTime created;

    @OneToOne(cascade=CascadeType.ALL)
    Person person;

    @JsonManagedReference
    @OneToMany(mappedBy = "userAccount")
    List<UserAccountNotification> userAccountNotifications;
}
