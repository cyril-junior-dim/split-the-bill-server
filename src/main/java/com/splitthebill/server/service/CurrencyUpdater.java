package com.splitthebill.server.service;

import com.splitthebill.server.dto.currency.CurrencyApiResponse;
import com.splitthebill.server.model.Currency;
import com.splitthebill.server.repository.CurrencyRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyUpdater {

    @NonNull
    private final RestTemplate restTemplate;

    @NonNull
    private final CurrencyRepository currencyRepository;

    @Value("${splitthebill.app.currencyUrl}")
    private String fetchUrl;

//    @Scheduled(initialDelay = 1000, fixedDelay = 86400000)
    public void updateCurrencies() {
        List<Currency> saveData = new ArrayList<>();
        CurrencyApiResponse response = this.restTemplate.getForObject(fetchUrl, CurrencyApiResponse.class);
        for (Map.Entry<String, BigDecimal> currencyRate : response.rates.entrySet()) {
            Currency currency;
            if (currencyRepository.existsByAbbreviation(currencyRate.getKey())) {
                currency = currencyRepository.findCurrencyByAbbreviation(currencyRate.getKey()).get();
                currency.setExchangeRate(currencyRate.getValue());
            } else {
                currency = new Currency(currencyRate.getKey(), currencyRate.getValue());
            }
            saveData.add(currency);
        }
        currencyRepository.saveAll(saveData);
    }
}
