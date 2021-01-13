package com.splitthebill.server.service;

import com.splitthebill.server.dto.GroupCreateDto;
import com.splitthebill.server.model.Group;
import com.splitthebill.server.repository.GroupRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class GroupService {

    @NonNull
    private final GroupRepository groupRepository;

    public Group createGroup(GroupCreateDto group) throws Exception {
        //TODO implement the behaviour
        return null;
    }

    public Group getGroupById(Long id) throws Exception {
        return groupRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
