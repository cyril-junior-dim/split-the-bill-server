package com.splitthebill.server.dto.group;

import com.splitthebill.server.model.user.PersonGroup;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PersonGroupReadDto {

    Long groupMemberId;
    Long personId;
    String name;
    BigDecimal memberBalance;

    public PersonGroupReadDto(PersonGroup member) {
        this.groupMemberId = member.getId();
        this.personId = member.getPerson().getId();
        this.name = member.getPerson().getName();
        this.memberBalance = member.getPersonGroupBalance();
    }
}
