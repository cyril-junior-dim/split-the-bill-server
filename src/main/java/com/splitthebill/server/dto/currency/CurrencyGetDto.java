package com.splitthebill.server.dto.currency;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class CurrencyGetDto {
    public String abbreviation;
    public BigDecimal exchangeRate;
}
