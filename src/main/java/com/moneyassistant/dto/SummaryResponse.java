package com.moneyassistant.dto;

import com.moneyassistant.domain.Category;

import java.math.BigDecimal;
import java.util.List;

public record SummaryResponse(
        String month,
        BigDecimal total,
        List<CategoryShare> byCategory,
        long transactionCount) {

    public record CategoryShare(Category category, BigDecimal total, int pct) {
    }
}
