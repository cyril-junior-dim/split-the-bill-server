package com.splitthebill.server.service;

import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.repository.PersonRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class PersonService {

    @NonNull
    private final PersonRepository personRepository;

    public Person getPersonById(Long id) throws EntityNotFoundException {
        return personRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

}
