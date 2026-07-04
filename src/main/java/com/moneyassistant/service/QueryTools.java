package com.moneyassistant.service;

import com.moneyassistant.domain.Category;
import com.moneyassistant.dto.CategoryTotal;
import com.moneyassistant.dto.ExpenseResponse;
import com.moneyassistant.repository.ExpenseRepository;
import com.moneyassistant.util.MonthRange;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Read-only query methods exposed to the LLM as tools. The model decides which
 * to call (and with what arguments) to answer a user's question; these methods
 * run the actual queries against Postgres. The LLM never writes to the DB and
 * never sees raw SQL.
 *
 * <p>Each invocation is recorded per-thread so the chat response can report which
 * tools were used. Spring AI executes tool calls synchronously on the calling
 * thread, so a simple ThreadLocal is sufficient.
 */
@Component
public class QueryTools {

    private final ExpenseRepository repo;
    private final ThreadLocal<List<String>> invoked = ThreadLocal.withInitial(ArrayList::new);

    public QueryTools(ExpenseRepository repo) {
        this.repo = repo;
    }

    void resetTrace() {
        invoked.get().clear();
    }

    List<String> trace() {
        return List.copyOf(invoked.get());
    }

    private void record(String tool) {
        invoked.get().add(tool);
    }

    @Tool(description = "Total amount spent in a given category for a given month. Month format: YYYY-MM.")
    public BigDecimal getSpendingByCategory(
            @ToolParam(description = "one of FOOD_DINING, GROCERIES, TRAVEL, SUBSCRIPTIONS, SHOPPING, UTILITIES, HEALTH, OTHER")
            Category category,
            @ToolParam(description = "month in YYYY-MM format") String month) {
        record("getSpendingByCategory");
        MonthRange r = MonthRange.of(month);
        return repo.sumByCategoryAndPeriod(category, r.start(), r.end());
    }

    @Tool(description = "Spending broken down by category for a given month, largest first. Month format: YYYY-MM.")
    public List<CategoryTotal> getMonthlyBreakdown(
            @ToolParam(description = "month in YYYY-MM format") String month) {
        record("getMonthlyBreakdown");
        MonthRange r = MonthRange.of(month);
        return repo.breakdown(r.start(), r.end());
    }

    @Tool(description = "The single largest expense in a given month. Month format: YYYY-MM.")
    public ExpenseResponse getLargestExpense(
            @ToolParam(description = "month in YYYY-MM format") String month) {
        record("getLargestExpense");
        MonthRange r = MonthRange.of(month);
        return repo.findFirstBySpentOnBetweenOrderByAmountDesc(r.start(), r.end())
                .map(ExpenseResponse::from)
                .orElse(null);
    }

    @Tool(description = "All recurring subscription charges on record.")
    public List<ExpenseResponse> getSubscriptions() {
        record("getSubscriptions");
        return repo.findByCategoryOrderBySpentOnDesc(Category.SUBSCRIPTIONS).stream()
                .map(ExpenseResponse::from)
                .toList();
    }
}
