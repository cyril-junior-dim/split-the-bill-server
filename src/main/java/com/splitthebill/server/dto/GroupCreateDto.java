package com.splitthebill.server.dto;

import lombok.NonNull;

import java.util.List;

public class GroupCreateDto {

    @NonNull
    public String name;

    @NonNull
    public List<Long> membersIds;

    //TODO String photoPath;

}
