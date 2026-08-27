package com.expensetracker.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.expensetracker.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

	List<Expense> findByUserId(Long userId);

	Page<Expense> findByUserId(Long userId, Pageable pageable);

	// ─── Totals ────────────────────────────────────────────────────────────────

	@Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
	BigDecimal getTotalExpenseByUser(@Param("userId") Long userId);

	@Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.date >= :startDate AND e.date <= :endDate")
	BigDecimal getTotalByDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	// ─── Date Range ────────────────────────────────────────────────────────────

	@Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.date >= :startDate AND e.date <= :endDate ORDER BY e.date DESC")
	List<Expense> getExpenseByDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	@Query("SELECT e FROM Expense e WHERE e.date >= :startDate AND e.date <= :endDate ORDER BY e.date DESC")
	List<Expense> getExpenseByDateRangeAllUsers(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	// ─── Period queries ────────────────────────────────────────────────────────

	@Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
	List<Expense> getExpenseByMonth(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

	@Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND YEAR(e.date) = :year")
	List<Expense> getExpenseByYear(@Param("userId") Long userId, @Param("year") int year);

	@Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.category = :category")
	List<Expense> getExpenseByCategory(@Param("userId") Long userId,
			@Param("category") com.expensetracker.enums.Categories category);

	// ─── Dashboard chart queries ───────────────────────────────────────────────

	@Query("SELECT e.category, SUM(e.amount), COUNT(e) FROM Expense e WHERE e.user.id = :userId GROUP BY e.category ORDER BY SUM(e.amount) DESC")
	List<Object[]> getCategoryBreakdown(@Param("userId") Long userId);

	@Query("SELECT MONTH(e.date), SUM(e.amount), COUNT(e) FROM Expense e WHERE e.user.id = :userId AND YEAR(e.date) = :year GROUP BY MONTH(e.date) ORDER BY MONTH(e.date)")
	List<Object[]> getMonthlyTrend(@Param("userId") Long userId, @Param("year") int year);

	@Query("SELECT e.date, SUM(e.amount), COUNT(e) FROM Expense e WHERE e.user.id = :userId AND MONTH(e.date) = :month AND YEAR(e.date) = :year GROUP BY e.date ORDER BY e.date")
	List<Object[]> getDailyTrend(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

	@Query("SELECT e FROM Expense e WHERE e.user.id = :userId ORDER BY e.amount DESC")
	List<Expense> getTop5Expenses(@Param("userId") Long userId, Pageable pageable);

	@Query("SELECT COUNT(e) FROM Expense e WHERE e.user.id = :userId")
	int countByUserId(@Param("userId") Long userId);
}