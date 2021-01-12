package com.splitthebill.server.model.user;


import com.splitthebill.server.dto.UserAccountCreateDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
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

    public UserAccount(UserAccountCreateDto accountCreateDto) {
        this.username = accountCreateDto.getUsername();
        this.password = accountCreateDto.getPassword();
        this.email = accountCreateDto.getEmail();
    }

}
