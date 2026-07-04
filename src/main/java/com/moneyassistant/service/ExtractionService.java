package com.moneyassistant.service;

import com.moneyassistant.dto.ExtractedExpense;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Turns a free-text note into a structured expense using the LLM's structured
 * output support. All numeric/category work stays type-safe: the model fills a
 * {@link ExtractedExpense} record directly.
 */
@Service
public class ExtractionService {

    private static final String SYSTEM = """
            You extract a single structured expense from a short free-text note.
            Today's date is {today}.

            Rules:
            - merchant: the payee/merchant if present, otherwise null.
            - amount: the numeric amount actually spent. Never invent an amount.
            - category: choose the single best fit from this fixed set only:
              FOOD_DINING, GROCERIES, TRAVEL, SUBSCRIPTIONS, SHOPPING, UTILITIES, HEALTH, OTHER.
            - spentOn: resolve to an absolute date (YYYY-MM-DD). If no date is
              mentioned, use today's date.
            """;

    private final ChatClient chatClient;

    public ExtractionService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public ExtractedExpense extract(String text) {
        return chatClient.prompt()
                .system(s -> s.text(SYSTEM).param("today", LocalDate.now().toString()))
                .user(text)
                .call()
                .entity(ExtractedExpense.class);
    }
}
