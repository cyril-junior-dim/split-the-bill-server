package com.splitthebill.server.model;


import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true)
    @ToString.Include
    private String abbreviation;

    @Column(precision = 19, scale = 9)
    @NonNull
    private BigDecimal exchangeRate;

    @Override
    public String toString() {
        return abbreviation;
    }
}
