package com.moneyassistant.service;

import com.moneyassistant.domain.Category;
import com.moneyassistant.dto.CategoryTotal;
import com.moneyassistant.dto.ExtractedExpense;
import com.moneyassistant.dto.SummaryResponse;
import com.moneyassistant.repository.ExpenseRepository;
import com.moneyassistant.web.ExtractionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    ExpenseRepository repo;

    @Mock
    ExtractionService extraction;

    @InjectMocks
    ExpenseService service;

    @Test
    void summaryComputesTotalAndRoundedPercentages() {
        when(repo.breakdown(any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of(
                new CategoryTotal(Category.TRAVEL, new BigDecimal("4020")),
                new CategoryTotal(Category.FOOD_DINING, new BigDecimal("2980"))));
        when(repo.countBySpentOnBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(5L);

        SummaryResponse summary = service.summary("2026-06");

        assertThat(summary.month()).isEqualTo("2026-06");
        assertThat(summary.total()).isEqualByComparingTo("7000");
        assertThat(summary.transactionCount()).isEqualTo(5);
        assertThat(summary.byCategory()).hasSize(2);
        // 4020 / 7000 = 57.4% -> 57 ; 2980 / 7000 = 42.6% -> 43
        assertThat(summary.byCategory().get(0).pct()).isEqualTo(57);
        assertThat(summary.byCategory().get(1).pct()).isEqualTo(43);
    }

    @Test
    void summaryHandlesNoExpenses() {
        when(repo.breakdown(any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of());
        when(repo.countBySpentOnBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(0L);

        SummaryResponse summary = service.summary("2026-06");

        assertThat(summary.total()).isEqualByComparingTo("0");
        assertThat(summary.byCategory()).isEmpty();
        assertThat(summary.transactionCount()).isZero();
    }

    @Test
    void createRejectsExtractionWithNoAmount() {
        when(extraction.extract("gibberish")).thenReturn(
                new ExtractedExpense(null, null, null, null));

        assertThatThrownBy(() -> service.createFromText("gibberish"))
                .isInstanceOf(ExtractionException.class);
    }
}
