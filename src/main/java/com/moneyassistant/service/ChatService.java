package com.moneyassistant.service;

import com.moneyassistant.dto.ChatReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Answers natural-language questions about spending. The LLM is given the
 * {@link QueryTools} and decides which to call — this is the function-calling
 * loop, driven by the model, not by hand-written branching.
 */
@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private static final String SYSTEM = """
            You are a helpful personal-finance assistant. Answer questions about the
            user's spending ONLY using the provided tools. Never invent numbers; if a
            tool returns nothing, say so.

            Today's date is {today}, so "this month" means the current calendar month.
            Amounts are in Indian Rupees (INR). Keep answers concise and friendly.
            """;

    private final ChatClient chatClient;
    private final QueryTools tools;

    public ChatService(ChatClient chatClient, QueryTools tools) {
        this.chatClient = chatClient;
        this.tools = tools;
    }

    public ChatReply chat(String message) {
        tools.resetTrace();
        try {
            String answer = chatClient.prompt()
                    .system(s -> s.text(SYSTEM).param("today", LocalDate.now().toString()))
                    .user(message)
                    .tools(tools)
                    .call()
                    .content();
            return new ChatReply(answer, tools.trace());
        } catch (Exception e) {
            // Degrade gracefully — never surface a 500 to the user for an LLM hiccup.
            log.warn("Chat request failed", e);
            return new ChatReply(
                    "Sorry — I couldn't reach the assistant right now. Please try again in a moment.",
                    List.of());
        }
    }
}
