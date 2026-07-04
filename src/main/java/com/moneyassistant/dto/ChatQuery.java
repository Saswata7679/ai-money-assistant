package com.moneyassistant.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatQuery(
        @NotBlank(message = "message must not be blank") String message) {
}
