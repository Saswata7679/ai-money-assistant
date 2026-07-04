package com.moneyassistant.service;

import com.moneyassistant.domain.Category;
import com.moneyassistant.domain.Expense;
import com.moneyassistant.dto.CategoryTotal;
import com.moneyassistant.dto.ExpenseResponse;
import com.moneyassistant.dto.ExtractedExpense;
import com.moneyassistant.dto.SummaryResponse;
import com.moneyassistant.repository.ExpenseRepository;
import com.moneyassistant.util.MonthRange;
import com.moneyassistant.web.ExtractionException;
import com.moneyassistant.web.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repo;
    private final ExtractionService extraction;

    public ExpenseService(ExpenseRepository repo, ExtractionService extraction) {
        this.repo = repo;
        this.extraction = extraction;
    }

    @Transactional
    public ExpenseResponse createFromText(String text) {
        ExtractedExpense ex = extraction.extract(text);
        if (ex == null || ex.amount() == null || ex.amount().signum() <= 0) {
            throw new ExtractionException("Could not extract a valid amount from: \"" + text + "\"");
        }
        Category category = ex.category() != null ? ex.category() : Category.OTHER;
        LocalDate spentOn = ex.spentOn() != null ? ex.spentOn() : LocalDate.now();
        Expense saved = repo.save(new Expense(text, ex.merchant(), ex.amount(), category, spentOn));
        return ExpenseResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> list(Category category, LocalDate from, LocalDate to) {
        return repo.search(category, from, to).stream().map(ExpenseResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SummaryResponse summary(String month) {
        MonthRange range = month != null ? MonthRange.of(month) : MonthRange.currentMonth();
        List<CategoryTotal> totals = repo.breakdown(range.start(), range.end());
        BigDecimal total = totals.stream()
                .map(CategoryTotal::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<SummaryResponse.CategoryShare> shares = totals.stream()
                .map(ct -> new SummaryResponse.CategoryShare(ct.category(), ct.total(), pct(ct.total(), total)))
                .toList();
        long count = repo.countBySpentOnBetween(range.start(), range.end());
        String label = month != null ? month : YearMonth.now().toString();
        return new SummaryResponse(label, total, shares, count);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new NotFoundException("Expense " + id + " not found");
        }
        repo.deleteById(id);
    }

    /** Percentage of {@code total} that {@code part} represents, rounded to a whole number. */
    static int pct(BigDecimal part, BigDecimal total) {
        if (total == null || total.signum() == 0) {
            return 0;
        }
        return part.multiply(BigDecimal.valueOf(100))
                .divide(total, 0, RoundingMode.HALF_UP)
                .intValue();
    }
}
