package com.splitthebill.server.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "`group`")
public class Group {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String photoPath;

    @OneToMany(mappedBy = "group")
    private List<PersonGroup> members;

    @OneToMany(mappedBy = "group")
    private List<GroupExpense> expenses;

}
