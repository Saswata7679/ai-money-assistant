package com.moneyassistant.service;

import com.moneyassistant.domain.Category;
import com.moneyassistant.domain.Expense;
import com.moneyassistant.dto.CategoryTotal;
import com.moneyassistant.dto.InsightsResponse;
import com.moneyassistant.dto.InsightsResult;
import com.moneyassistant.repository.ExpenseRepository;
import com.moneyassistant.util.MonthRange;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Generates a short monthly spending review. Facts (the numbers) are computed in
 * plain Java; the LLM only phrases them. Cached by month so repeated dashboard
 * loads don't re-bill the model.
 */
@Service
public class InsightsService {

    private static final String SYSTEM = """
            You write a short monthly spending review from the numbers provided.
            Do not invent any figures — use only what is given.
            Produce a 2-3 sentence summary and 2-4 short highlight bullet strings.
            Amounts are in Indian Rupees (INR).
            """;

    private final ChatClient chatClient;
    private final ExpenseRepository repo;

    public InsightsService(ChatClient chatClient, ExpenseRepository repo) {
        this.chatClient = chatClient;
        this.repo = repo;
    }

    @Cacheable(value = "insights", key = "#month")
    public InsightsResponse forMonth(String month) {
        MonthRange range = MonthRange.of(month);
        List<CategoryTotal> breakdown = repo.breakdown(range.start(), range.end());
        List<Expense> subscriptions = repo.findByCategoryOrderBySpentOnDesc(Category.SUBSCRIPTIONS);

        String facts = buildFacts(month, breakdown, subscriptions);

        InsightsResult result = chatClient.prompt()
                .system(SYSTEM)
                .user(facts)
                .call()
                .entity(InsightsResult.class);

        return new InsightsResponse(month, result.summary(), result.highlights());
    }

    private String buildFacts(String month, List<CategoryTotal> breakdown, List<Expense> subscriptions) {
        StringBuilder sb = new StringBuilder();
        sb.append("Month: ").append(month).append("\n");
        sb.append("Spending by category (INR):\n");
        if (breakdown.isEmpty()) {
            sb.append("  (no expenses recorded)\n");
        } else {
            for (CategoryTotal ct : breakdown) {
                sb.append("  ").append(ct.category()).append(": ").append(ct.total()).append("\n");
            }
        }
        sb.append("Active subscriptions:\n");
        if (subscriptions.isEmpty()) {
            sb.append("  (none)\n");
        } else {
            for (Expense s : subscriptions) {
                sb.append("  ").append(s.getMerchant() == null ? "Unknown" : s.getMerchant())
                        .append(": ").append(s.getAmount()).append("\n");
            }
        }
        return sb.toString();
    }
}
