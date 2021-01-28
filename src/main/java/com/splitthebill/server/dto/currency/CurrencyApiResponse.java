package com.splitthebill.server.dto.currency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class CurrencyApiResponse {
    public boolean success;
    public LocalDate date;
    public String base;
    public Map<String, BigDecimal> rates;
}
