package com.moneyassistant.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

/**
 * The first and last calendar day of a month, derived from a "YYYY-MM" string.
 */
public record MonthRange(LocalDate start, LocalDate end) {

    public static MonthRange of(String month) {
        try {
            YearMonth ym = YearMonth.parse(month); // expects YYYY-MM
            return new MonthRange(ym.atDay(1), ym.atEndOfMonth());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid month '" + month + "', expected format YYYY-MM");
        }
    }

    public static MonthRange currentMonth() {
        YearMonth ym = YearMonth.now();
        return new MonthRange(ym.atDay(1), ym.atEndOfMonth());
    }
}
