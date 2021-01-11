package com.splitthebill.server.model;


import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String abbreviation;

    @Column(precision = 19, scale = 9)
    private BigDecimal exchangeRate;

}
