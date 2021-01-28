package com.splitthebill.server.service;

import com.splitthebill.server.dto.expense.OwnExpenseCreateDto;
import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.expense.OwnExpense;
import com.splitthebill.server.model.user.Person;
import com.splitthebill.server.repository.CurrencyRepository;
import com.splitthebill.server.repository.OwnExpenseRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class OwnExpenseService {

    @NonNull
    private OwnExpenseRepository ownExpenseRepository;

    @NonNull
    private CurrencyRepository currencyRepository;

    public OwnExpense addExpense(OwnExpenseCreateDto expenseDto, Person person){
        OwnExpense ownExpense = new OwnExpense();
        Currency currency = currencyRepository.findCurrencyByAbbreviation(
                expenseDto.getCurrencyAbbreviation())
                .orElseThrow(() -> new EntityNotFoundException("Currency not found"));
        ownExpense.setCurrency(currency);
        ownExpense.setOwner(person);
        ownExpense.setAmount(expenseDto.getAmount());
        ownExpense.setTitle(expenseDto.getTitle());
        ownExpense.setReceiptPhoto(expenseDto.getReceiptPhoto());
        person.addOwnExpense(ownExpense);
        return ownExpenseRepository.save(ownExpense);
    }

}
