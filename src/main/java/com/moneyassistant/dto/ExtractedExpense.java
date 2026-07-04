package com.moneyassistant.dto;

import com.moneyassistant.domain.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The shape the LLM must return during extraction. Spring AI maps the model's
 * JSON straight into this record via structured output ({@code .entity(...)}).
 */
public record ExtractedExpense(
        String merchant,
        BigDecimal amount,
        Category category,
        LocalDate spentOn) {
}
