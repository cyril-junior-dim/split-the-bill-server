package com.splitthebill.server.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
public class PersonGroup {

    @Id
    @GeneratedValue
    private Long id;

    private BigDecimal personGroupBalance;

    private Date joined;

    @ManyToOne
    private Person person;

    @ManyToOne
    private Group group;

}
