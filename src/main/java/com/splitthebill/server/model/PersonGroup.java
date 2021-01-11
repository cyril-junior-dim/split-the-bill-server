package com.splitthebill.server.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class PersonGroup {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    Person person;

    @ManyToOne
    Group group;

}
