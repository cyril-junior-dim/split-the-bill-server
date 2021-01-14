package com.splitthebill.server.model.user;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.splitthebill.server.dto.UserAccountCreateDto;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {

    //TODO isConfirmed? -> createPerson()

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @CreationTimestamp
    private LocalDateTime created;

    @OneToOne
    private Person person;

    @JsonManagedReference
    @OneToMany(mappedBy = "userAccount")
    private List<UserAccountNotification> userAccountNotifications;

    public UserAccount(UserAccountCreateDto accountCreateDto) {
        this.username = accountCreateDto.username;
        this.password = accountCreateDto.password;
        this.email = accountCreateDto.email;
    }

}
