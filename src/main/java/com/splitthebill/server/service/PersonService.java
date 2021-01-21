package com.splitthebill.server.service;

import com.splitthebill.server.dto.PersonCreateDto;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.repository.CurrencyRepository;
import com.splitthebill.server.repository.PersonRepository;
import com.splitthebill.server.repository.UserAccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class PersonService {

    @NonNull
    private final PersonRepository personRepository;

    @NonNull
    private final CurrencyRepository currencyRepository;

    public Person getPersonById(Long id) throws EntityNotFoundException {
        return personRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Person createPerson(UserAccount userAccount, PersonCreateDto personCreateDto) {
        Person person = new Person(userAccount, personCreateDto.name);
        person.getUserAccount().setPerson(person);
        person.setPreferredCurrency(currencyRepository.findCurrencyByAbbreviation("EUR").orElseThrow(EntityNotFoundException::new));
        return personRepository.save(person);
    }

    public Person updatePerson(Person existingPerson, PersonCreateDto newPerson) {
        existingPerson.setName(newPerson.name);
        return personRepository.save(existingPerson);
    }
}
