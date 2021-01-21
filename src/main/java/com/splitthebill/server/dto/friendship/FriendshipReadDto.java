package com.splitthebill.server.dto.friendship;

import lombok.AllArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
public class FriendshipReadDto extends RepresentationModel<FriendshipReadDto> {
    public String personName;
}
