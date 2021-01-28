package com.splitthebill.server.model.expense;

import com.splitthebill.server.model.Currency;
import com.splitthebill.server.model.user.Person;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class Expense {

    @NotBlank
    private String title;

    private BigDecimal amount;

    @CreationTimestamp
    private LocalDateTime created;

    private String receiptPhoto;

    @ManyToOne
    private Currency currency;

}
