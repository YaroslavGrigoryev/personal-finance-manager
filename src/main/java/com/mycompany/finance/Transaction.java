package com.mycompany.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal amount;
    private String category;
    private TransactionType type;
    private LocalDateTime createdAt;

    public Transaction(BigDecimal amount, String category, TransactionType type) {
        this.amount = amount;
        this.category = category;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public TransactionType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
