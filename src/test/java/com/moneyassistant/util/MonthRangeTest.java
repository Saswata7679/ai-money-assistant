package com.moneyassistant.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MonthRangeTest {

    @Test
    void parsesValidMonthToFirstAndLastDay() {
        MonthRange range = MonthRange.of("2026-06");

        assertThat(range.start()).isEqualTo(LocalDate.of(2026, 6, 1));
        assertThat(range.end()).isEqualTo(LocalDate.of(2026, 6, 30));
    }

    @Test
    void handlesFebruaryLeapYear() {
        MonthRange range = MonthRange.of("2024-02");

        assertThat(range.end()).isEqualTo(LocalDate.of(2024, 2, 29));
    }

    @Test
    void rejectsMalformedMonth() {
        assertThatThrownBy(() -> MonthRange.of("June 2026"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("YYYY-MM");
    }
}
