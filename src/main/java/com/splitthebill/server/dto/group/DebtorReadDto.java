package com.splitthebill.server.dto.group;

import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.user.PersonGroup;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class DebtorReadDto {

    Long debtorId;
    String name;
    int weight;

    public DebtorReadDto(Long debtorId, String name, int weight) {
        this.debtorId = debtorId;
        this.name = name;
        this.weight = weight;
    }
}
