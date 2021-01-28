package com.splitthebill.server.dto.expense;

import com.splitthebill.server.model.expense.OwnExpense;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class OwnExpenseReadDto {
    Long ownExpenseId;
    String title;
    BigDecimal amount;
    LocalDateTime created;
    String receiptPhoto;
    String currencyAbbreviation;

    public OwnExpenseReadDto(OwnExpense ownExpense) {
        this.ownExpenseId = ownExpense.getId();
        this.title = ownExpense.getTitle();
        this.amount = ownExpense.getAmount();
        this.created = ownExpense.getCreated();
        this.receiptPhoto = ownExpense.getReceiptPhoto();
        this.currencyAbbreviation = ownExpense.getCurrency().getAbbreviation();
    }
}