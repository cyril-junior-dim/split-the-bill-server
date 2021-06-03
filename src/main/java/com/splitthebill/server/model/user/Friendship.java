package com.splitthebill.server.model.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @NonNull
    private Person person1;

    @ManyToOne
    @NonNull
    private Person person2;

    @NonNull
    private boolean confirmed;

    private LocalDateTime date;

    public boolean relatesToSamePeopleAs(Friendship other){
        return person1.equals(other.person2) && person2.equals(other.person1);
    }
}
