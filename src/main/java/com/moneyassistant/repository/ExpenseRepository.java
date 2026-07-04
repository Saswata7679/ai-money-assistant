package com.moneyassistant.repository;

import com.moneyassistant.domain.Category;
import com.moneyassistant.domain.Expense;
import com.moneyassistant.dto.CategoryTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("""
           select e from Expense e
           where (:category is null or e.category = :category)
             and (:from is null or e.spentOn >= :from)
             and (:to   is null or e.spentOn <= :to)
           order by e.spentOn desc, e.id desc
           """)
    List<Expense> search(Category category, LocalDate from, LocalDate to);

    List<Expense> findByCategoryOrderBySpentOnDesc(Category category);

    long countBySpentOnBetween(LocalDate start, LocalDate end);

    Optional<Expense> findFirstBySpentOnBetweenOrderByAmountDesc(LocalDate start, LocalDate end);

    @Query("""
           select coalesce(sum(e.amount), 0) from Expense e
           where e.category = :category and e.spentOn between :start and :end
           """)
    BigDecimal sumByCategoryAndPeriod(Category category, LocalDate start, LocalDate end);

    @Query("""
           select new com.moneyassistant.dto.CategoryTotal(e.category, sum(e.amount))
           from Expense e
           where e.spentOn between :start and :end
           group by e.category
           order by sum(e.amount) desc
           """)
    List<CategoryTotal> breakdown(LocalDate start, LocalDate end);
}
