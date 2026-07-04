package com.moneyassistant.dto;

import java.util.List;

public record InsightsResponse(String month, String summary, List<String> highlights) {
}
