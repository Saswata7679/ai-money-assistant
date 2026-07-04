package com.moneyassistant.dto;

import java.util.List;

/** Shape the LLM returns for insights (structured output). */
public record InsightsResult(String summary, List<String> highlights) {
}
