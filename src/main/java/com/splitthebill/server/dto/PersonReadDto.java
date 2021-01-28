package com.splitthebill.server.dto;

import com.splitthebill.server.model.Currency;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
public class PersonReadDto extends RepresentationModel<PersonReadDto> {

    public Long id;
    public String name;
    public Map<Currency, BigDecimal> balances;
}
