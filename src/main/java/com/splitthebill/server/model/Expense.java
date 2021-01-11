package com.splitthebill.server.model;

import lombok.Data;

import java.util.Date;

@Data
public abstract class Expense {

    private String title;

    private double amount;

    private Date created;

    private String receiptPhoto;

    private Person creditor;

}
