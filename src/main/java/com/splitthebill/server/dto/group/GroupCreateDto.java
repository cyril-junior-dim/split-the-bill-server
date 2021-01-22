package com.splitthebill.server.dto.group;

import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class GroupCreateDto {

    @NotBlank
    @Size(min = 2)
    public String name;

    @NonNull
    public List<Long> membersIds;

    //TODO String photoPath;

}
