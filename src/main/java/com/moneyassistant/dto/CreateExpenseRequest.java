package com.moneyassistant.dto;

import jakarta.validation.constraints.NotBlank;

/** Free-text expense entry, e.g. "dinner at Barbeque Nation 2400". */
public record CreateExpenseRequest(
        @NotBlank(message = "text must not be blank") String text) {
}
