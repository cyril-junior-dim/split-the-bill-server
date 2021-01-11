package com.splitthebill.server.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Group {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String photoPath;

    @ManyToMany
    private List<PersonGroup> members;

    @OneToMany
    private List<GroupExpense> expenses;

}
