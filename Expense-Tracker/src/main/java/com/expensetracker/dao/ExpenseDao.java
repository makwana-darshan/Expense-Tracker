package com.expensetracker.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.expensetracker.entity.Expense;
import com.expensetracker.repository.ExpenseRepository;

@Repository
public class ExpenseDao {

	@Autowired
	private ExpenseRepository expenseRepository;

	// Save expense
	public Expense saveExpense(Expense expense) {
		return expenseRepository.save(expense);
	}

	// Get all expenses by user
	public List<Expense> getAllExpenseByUser(Long userId) {
		return expenseRepository.findByUserId(userId);
	}

	// Get expense by ID
	public Expense getExpenseById(Long id) {
		return expenseRepository.findById(id).orElse(null);
	}

	// Update expense
	public Expense updateExpense(Expense expense) {
		return expenseRepository.save(expense);
	}

	// Delete expense
	public void deleteExpense(Long id) {
		expenseRepository.deleteById(id);
	}

	// Total expense
	public BigDecimal getTotalExpenseByUser(Long userId) {
		BigDecimal total = expenseRepository.getTotalExpenseByUser(userId);
		return total != null ? total : BigDecimal.ZERO;
	}

	// Weekly expenses
	public List<Expense> getWeeklyExpenses(Long userId) {
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(7);
		return expenseRepository.getExpenseByDateRange(userId, startDate, endDate);
	}

	// Monthly expenses
	public List<Expense> getMonthlyExpenses(Long userId, int month, int year) {
		return expenseRepository.getExpenseByMonth(userId, month, year);
	}

	// Yearly expenses
	public List<Expense> getYearlyExpenses(Long userId, int year) {
		return expenseRepository.getExpenseByYear(userId, year);
	}

	// Category wise expenses
	public List<Expense> getExpenseByCategory(Long userId, String category) {
		return expenseRepository.getExpenseByCategory(userId, category);
	}
}