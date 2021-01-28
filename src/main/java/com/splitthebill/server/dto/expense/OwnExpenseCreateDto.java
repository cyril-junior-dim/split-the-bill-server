package com.splitthebill.server.dto.expense;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Data
public class OwnExpenseCreateDto {

    @NotBlank
    String title;

    @DecimalMin("0.01")
    BigDecimal amount;

    String receiptPhoto;

    @NotEmpty
    String currencyAbbreviation;

}