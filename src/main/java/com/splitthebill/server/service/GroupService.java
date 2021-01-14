package com.splitthebill.server.service;

import com.splitthebill.server.dto.GroupCreateDto;
import com.splitthebill.server.model.Group;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.PersonGroup;
import com.splitthebill.server.repository.GroupRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class GroupService {

    @NonNull
    private final GroupRepository groupRepository;

    @NonNull
    private final PersonService personService;

    public Group createGroup(GroupCreateDto groupDto) throws EntityNotFoundException {
        Group group = new Group();
        group.setName(groupDto.name);
        LinkedList<PersonGroup> members = new LinkedList<>();
        for (Long id : groupDto.membersIds) {
            Person person = personService.getPersonById(id);
            PersonGroup groupPerson = new PersonGroup(person, group);
            members.add(groupPerson);
        }
        group.setMembers(members);
        return group;
    }

    public Group getGroupById(Long id) throws Exception {
        return groupRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
