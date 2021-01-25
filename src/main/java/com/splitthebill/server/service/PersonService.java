package com.splitthebill.server.service;

import com.splitthebill.server.dto.PersonCreateDto;
import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.model.user.UserAccount;
import com.splitthebill.server.repository.CurrencyRepository;
import com.splitthebill.server.repository.PersonRepository;
import com.splitthebill.server.repository.UserAccountRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

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

    public Person createPerson(UserAccount userAccount, PersonCreateDto personCreateDto) throws EntityNotFoundException {
        Person person = new Person(userAccount, personCreateDto.name);
        person.getUserAccount().setPerson(person);
        person.setPreferredCurrency(currencyRepository.findCurrencyByAbbreviation(personCreateDto.currencyAbbreviation)
                .orElseThrow(EntityNotFoundException::new));
        person.getBalances().put(person.getPreferredCurrency(), BigDecimal.ZERO);
        return personRepository.save(person);
    }

    public Person updatePerson(Person existingPerson, PersonCreateDto newPerson) throws EntityNotFoundException {
        existingPerson.setName(newPerson.name);
        Currency newPreferredCurrency = currencyRepository.findCurrencyByAbbreviation(newPerson.currencyAbbreviation)
                .orElseThrow(EntityNotFoundException::new);
        if(existingPerson.getBalances().get(existingPerson.getPreferredCurrency()).equals(BigDecimal.ZERO))
            existingPerson.getBalances().remove(existingPerson.getPreferredCurrency());
        existingPerson.getBalances().putIfAbsent(newPreferredCurrency, BigDecimal.ZERO);
        existingPerson.setPreferredCurrency(newPreferredCurrency);
        return personRepository.save(existingPerson);
    }
}
