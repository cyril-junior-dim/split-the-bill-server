package com.splitthebill.server.dto.group;

import com.splitthebill.server.model.Group;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class GroupReadDto {

    Long id;
    String name;
    String photoPath;
    List<PersonGroupReadDto> members = new LinkedList<>();
    List<GroupExpenseReadDto> expenses = new LinkedList<>();

    public GroupReadDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.photoPath = group.getPhotoPath();
        for(var member: group.getMembers()){
            members.add(new PersonGroupReadDto(member));
        }
        for(var expense: group.getExpenses()){
            expenses.add(new GroupExpenseReadDto(expense));
        }
    }
}
