package com.splitthebill.server.model.user;

import com.splitthebill.server.dto.ThirdPartyUserAccountCreateDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ThirdPartyUserAccount extends UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;

    public ThirdPartyUserAccount(ThirdPartyUserAccountCreateDto accountCreateDto){
        this.setEmail(accountCreateDto.email);
        this.setProvider(accountCreateDto.provider);
        this.setPerson(new Person(this, accountCreateDto.name));
    }

    @Builder
    public ThirdPartyUserAccount(Long id, String email, LocalDateTime created, Person person,
                            List<UserAccountNotification> userAccountNotifications,
                            String provider){
        setId(id);
        setEmail(email);
        setPerson(person);
        setUserAccountNotifications(userAccountNotifications);
        setCreated(created);
        setProvider(provider);
    }
}
