package com.splitthebill.server.dto.currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public class CurrencyApiResponse {
    public boolean success;
    public LocalDateTime date;
    public String base;
    public Map<String, BigDecimal> rates;
}
