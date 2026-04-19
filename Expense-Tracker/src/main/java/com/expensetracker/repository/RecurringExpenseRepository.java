package com.expensetracker.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.expensetracker.entity.RecurringExpense;

public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Long> {

	List<RecurringExpense> findByUserId(Long userId);

	List<RecurringExpense> findByUserIdAndActive(Long userId, Boolean active);

	// Scheduler query: all active records whose nextDueDate is today or earlier
	@Query("SELECT r FROM RecurringExpense r WHERE r.active = true AND r.nextDueDate <= :today")
	List<RecurringExpense> findDueRecurring(@Param("today") LocalDate today);
}