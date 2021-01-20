package com.splitthebill.server.controller;

import com.splitthebill.server.dto.currency.CurrencyGetDto;
import com.splitthebill.server.model.Currency;
import com.splitthebill.server.repository.CurrencyRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/currencies")
@RequiredArgsConstructor
public class CurrencyController {

    @NonNull
    private CurrencyRepository currencyRepository;

    @RequestMapping("/{abbreviation}")
    public ResponseEntity<?> getCurrencyByAbbreviation(@PathVariable String abbreviation){
        Currency currency = currencyRepository.findCurrencyByAbbreviation(abbreviation).orElse(null);
        return currency == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(new CurrencyGetDto(currency.getAbbreviation(), currency.getExchangeRate()));
    }
}
