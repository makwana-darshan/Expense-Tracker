package com.expensetracker.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.expensetracker.entity.Expense;
import com.expensetracker.repository.ExpenseRepository;

@Repository
public class ExpenseDao {

	@Autowired
	private ExpenseRepository expenseRepository;

	public Expense saveExpense(Expense expense) {
		return expenseRepository.save(expense);
	}

	public List<Expense> getAllExpenseByUser(Long userId) {
		return expenseRepository.findByUserId(userId);
	}

	public List<Expense> getAllExpenses() {
		return expenseRepository.findAll();
	}

	public Expense getExpenseById(Long id) {
		return expenseRepository.findById(id).orElse(null);
	}

	public Expense updateExpense(Expense expense) {
		return expenseRepository.save(expense);
	}

	public void deleteExpense(Long id) {
		expenseRepository.deleteById(id);
	}

	public BigDecimal getTotalExpenseByUser(Long userId) {
		BigDecimal total = expenseRepository.getTotalExpenseByUser(userId);
		return total != null ? total : BigDecimal.ZERO;
	}

	// ─── Date Range ────────────────────────────────────────────────────────────

	public List<Expense> getExpenseByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
		return expenseRepository.getExpenseByDateRange(userId, startDate, endDate);
	}

	public BigDecimal getTotalByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
		BigDecimal total = expenseRepository.getTotalByDateRange(userId, startDate, endDate);
		return total != null ? total : BigDecimal.ZERO;
	}

	public List<Expense> getExpenseByDateRangeAllUsers(LocalDate startDate, LocalDate endDate) {
		return expenseRepository.getExpenseByDateRangeAllUsers(startDate, endDate);
	}

	// ─── Report methods ────────────────────────────────────────────────────────

	public List<Expense> getWeeklyExpenses(Long userId) {
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(7);
		return expenseRepository.getExpenseByDateRange(userId, startDate, endDate);
	}

	public List<Expense> getMonthlyExpenses(Long userId, int month, int year) {
		return expenseRepository.getExpenseByMonth(userId, month, year);
	}

	public List<Expense> getYearlyExpenses(Long userId, int year) {
		return expenseRepository.getExpenseByYear(userId, year);
	}

	public List<Expense> getExpenseByCategory(Long userId, String category) {
		return expenseRepository.getExpenseByCategory(userId, category);
	}

	public Page<Expense> getAllExpenseByUserPaginated(Long userId, int page, int size, String sortBy, String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sort);
		return expenseRepository.findByUserId(userId, pageable);
	}

	// ─── Dashboard chart methods ───────────────────────────────────────────────

	public List<Object[]> getCategoryBreakdown(Long userId) {
		return expenseRepository.getCategoryBreakdown(userId);
	}

	public List<Object[]> getMonthlyTrend(Long userId, int year) {
		return expenseRepository.getMonthlyTrend(userId, year);
	}

	public List<Object[]> getDailyTrend(Long userId, int month, int year) {
		return expenseRepository.getDailyTrend(userId, month, year);
	}

	public List<Expense> getTop5Expenses(Long userId) {
		return expenseRepository.getTop5Expenses(userId, PageRequest.of(0, 5));
	}

	public int countByUserId(Long userId) {
		return expenseRepository.countByUserId(userId);
	}
}