package com.moneyassistant.bootstrap;

import com.moneyassistant.domain.Category;
import com.moneyassistant.domain.Expense;
import com.moneyassistant.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Seeds a handful of demo expenses on first run (only when the table is empty)
 * so the chat and insights endpoints have real data to query. Dates are relative
 * to today, spread across this month and last month.
 */
@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ExpenseRepository repo;

    public DataSeeder(ExpenseRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) {
            return;
        }
        LocalDate today = LocalDate.now();
        LocalDate thisMonth = today.withDayOfMonth(1);
        LocalDate lastMonth = thisMonth.minusMonths(1);

        List<Expense> demo = List.of(
                // ---- this month ----
                expense("dinner at Barbeque Nation 2400", "Barbeque Nation", "2400", Category.FOOD_DINING, thisMonth.plusDays(2)),
                expense("swiggy biryani 480", "Swiggy", "480", Category.FOOD_DINING, thisMonth.plusDays(5)),
                expense("uber to airport 620", "Uber", "620", Category.TRAVEL, thisMonth.plusDays(6)),
                expense("flight to Delhi 3400", "IndiGo", "3400", Category.TRAVEL, thisMonth.plusDays(8)),
                expense("bigbasket weekly groceries 2310", "BigBasket", "2310", Category.GROCERIES, thisMonth.plusDays(3)),
                expense("netflix monthly subscription 649", "Netflix", "649", Category.SUBSCRIPTIONS, thisMonth.plusDays(1)),
                expense("spotify subscription 119", "Spotify", "119", Category.SUBSCRIPTIONS, thisMonth.plusDays(1)),
                expense("gym app subscription 1229", "FitApp", "1229", Category.SUBSCRIPTIONS, thisMonth.plusDays(1)),
                expense("electricity bill 1450", "BESCOM", "1450", Category.UTILITIES, thisMonth.plusDays(9)),
                expense("new headphones 1899", "Amazon", "1899", Category.SHOPPING, thisMonth.plusDays(4)),
                // ---- last month ----
                expense("dinner out 1600", "Local Diner", "1600", Category.FOOD_DINING, lastMonth.plusDays(10)),
                expense("cab rides 900", "Ola", "900", Category.TRAVEL, lastMonth.plusDays(12)),
                expense("groceries 2100", "BigBasket", "2100", Category.GROCERIES, lastMonth.plusDays(6)),
                expense("netflix subscription 649", "Netflix", "649", Category.SUBSCRIPTIONS, lastMonth.plusDays(1)),
                expense("pharmacy 540", "Apollo", "540", Category.HEALTH, lastMonth.plusDays(15))
        );

        repo.saveAll(demo);
        log.info("Seeded {} demo expenses", demo.size());
    }

    private static Expense expense(String raw, String merchant, String amount, Category category, LocalDate date) {
        return new Expense(raw, merchant, new BigDecimal(amount), category, date);
    }
}
