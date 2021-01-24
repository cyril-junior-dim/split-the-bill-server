package com.splitthebill.server.dto.friendship;

import org.springframework.hateoas.RepresentationModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendshipCollectionReadDto extends RepresentationModel<FriendshipCollectionReadDto> {

    public Map<String, List<FriendshipReadDto>> friendships = new HashMap<>();
}
