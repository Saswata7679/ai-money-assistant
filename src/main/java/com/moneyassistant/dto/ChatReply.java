package com.moneyassistant.dto;

import java.util.List;

/**
 * The assistant's answer, plus which query tools the LLM chose to call
 * (handy for demos and debugging the agent loop).
 */
public record ChatReply(String answer, List<String> toolsUsed) {
}
