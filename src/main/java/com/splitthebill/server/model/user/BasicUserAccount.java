package com.splitthebill.server.model.user;

import com.splitthebill.server.dto.BasicUserAccountCreateDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
public class BasicUserAccount extends UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //TODO isConfirmed? -> createPerson()

    @Column(unique = true)
    private String username;

    private String password;

    public BasicUserAccount(BasicUserAccountCreateDto accountCreateDto) {
        this.username = accountCreateDto.username;
        this.password = accountCreateDto.password;
        this.setEmail(accountCreateDto.email);
    }

    @Builder
    public BasicUserAccount(Long id, String email, LocalDateTime created, Person person,
                            List<UserAccountNotification> userAccountNotifications,
                            String username, String password){
        setEmail(email);
        setPerson(person);
        setUserAccountNotifications(userAccountNotifications);
        setCreated(created);
        setId(id);
        setUsername(username);
        setPassword(password);
    }
}
