package com.expensetracker.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.expensetracker.dao.ExpenseDao;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.Expense;

@Service
public class ReportService {

	@Autowired
	private ExpenseDao expenseDao;

	// Total expense
	public ResponseEntity<ResponseStructure<BigDecimal>> getTotalExpense(Long userId) {
		ResponseStructure<BigDecimal> response = new ResponseStructure<>();
		BigDecimal total = expenseDao.getTotalExpenseByUser(userId);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Total expense fetched successfully");
		response.setData(total);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Weekly report
	public ResponseEntity<ResponseStructure<List<Expense>>> getWeeklyReport(Long userId) {
		ResponseStructure<List<Expense>> response = new ResponseStructure<>();
		List<Expense> expenses = expenseDao.getWeeklyExpenses(userId);
		BigDecimal total = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Weekly total: ₹" + total);
		response.setData(expenses);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Monthly report
	public ResponseEntity<ResponseStructure<List<Expense>>> getMonthlyReport(Long userId, int month, int year) {
		ResponseStructure<List<Expense>> response = new ResponseStructure<>();
		List<Expense> expenses = expenseDao.getMonthlyExpenses(userId, month, year);
		BigDecimal total = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Monthly total: ₹" + total);
		response.setData(expenses);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Yearly report
	public ResponseEntity<ResponseStructure<List<Expense>>> getYearlyReport(Long userId, int year) {
		ResponseStructure<List<Expense>> response = new ResponseStructure<>();
		List<Expense> expenses = expenseDao.getYearlyExpenses(userId, year);
		BigDecimal total = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Yearly total: ₹" + total);
		response.setData(expenses);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Category wise report
	public ResponseEntity<ResponseStructure<List<Expense>>> getCategoryReport(Long userId, String category) {
		ResponseStructure<List<Expense>> response = new ResponseStructure<>();
		List<Expense> expenses = expenseDao.getExpenseByCategory(userId, category);
		BigDecimal total = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Category (" + category + ") total: ₹" + total);
		response.setData(expenses);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}