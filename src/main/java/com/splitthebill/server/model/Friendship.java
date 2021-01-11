package com.splitthebill.server.model;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@Data
public class Friendship {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Person person1;

    @ManyToOne
    private Person person2;

    private Date date;
}
