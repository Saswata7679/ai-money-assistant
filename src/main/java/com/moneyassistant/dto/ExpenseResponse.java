package com.moneyassistant.dto;

import com.moneyassistant.domain.Category;
import com.moneyassistant.domain.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(
        Long id,
        String merchant,
        BigDecimal amount,
        Category category,
        LocalDate spentOn,
        String rawText) {

    public static ExpenseResponse from(Expense e) {
        return new ExpenseResponse(
                e.getId(), e.getMerchant(), e.getAmount(),
                e.getCategory(), e.getSpentOn(), e.getRawText());
    }
}
