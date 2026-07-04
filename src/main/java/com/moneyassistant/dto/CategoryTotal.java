package com.moneyassistant.dto;

import com.moneyassistant.domain.Category;

import java.math.BigDecimal;

/** A category and its summed amount. Also used directly in a JPQL constructor query. */
public record CategoryTotal(Category category, BigDecimal total) {
}
