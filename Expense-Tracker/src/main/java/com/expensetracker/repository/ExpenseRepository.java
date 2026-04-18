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

	// Total expense by user
	@Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
	BigDecimal getTotalExpenseByUser(@Param("userId") Long userId);

	// Weekly expenses
	@Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.date >= :startDate AND e.date <= :endDate")
	List<Expense> getExpenseByDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	// Monthly expenses
	@Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND MONTH(e.date) = :month AND YEAR(e.date) = :year")
	List<Expense> getExpenseByMonth(@Param("userId") Long userId, @Param("month") int month, @Param("year") int year);

	// Yearly expenses
	@Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND YEAR(e.date) = :year")
	List<Expense> getExpenseByYear(@Param("userId") Long userId, @Param("year") int year);

	// Category wise expenses
	@Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.category = :category")
	List<Expense> getExpenseByCategory(@Param("userId") Long userId, @Param("category") String category);
}