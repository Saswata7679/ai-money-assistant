package com.moneyassistant.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "raw_text", nullable = false, columnDefinition = "text")
    private String rawText;

    @Column(name = "merchant", length = 120)
    private String merchant;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 40)
    private Category category;

    @Column(name = "spent_on", nullable = false)
    private LocalDate spentOn;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected Expense() {
        // for JPA
    }

    public Expense(String rawText, String merchant, BigDecimal amount, Category category, LocalDate spentOn) {
        this.rawText = rawText;
        this.merchant = merchant;
        this.amount = amount;
        this.category = category;
        this.spentOn = spentOn;
    }

    public Long getId() {
        return id;
    }

    public String getRawText() {
        return rawText;
    }

    public String getMerchant() {
        return merchant;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getSpentOn() {
        return spentOn;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
