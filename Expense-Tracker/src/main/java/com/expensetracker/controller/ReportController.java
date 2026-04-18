package com.expensetracker.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.Expense;
import com.expensetracker.service.ReportService;

@RestController
@RequestMapping("/report")
public class ReportController {

	@Autowired
	private ReportService reportService;

	@GetMapping("/total/{userId}")
	public ResponseEntity<ResponseStructure<BigDecimal>> getTotalExpense(@PathVariable Long userId) {
		return reportService.getTotalExpense(userId);
	}

	@GetMapping("/weekly/{userId}")
	public ResponseEntity<ResponseStructure<List<Expense>>> getWeeklyReport(@PathVariable Long userId) {
		return reportService.getWeeklyReport(userId);
	}

	@GetMapping("/monthly/{userId}/{month}/{year}")
	public ResponseEntity<ResponseStructure<List<Expense>>> getMonthlyReport(@PathVariable Long userId,
			@PathVariable int month, @PathVariable int year) {
		return reportService.getMonthlyReport(userId, month, year);
	}

	@GetMapping("/yearly/{userId}/{year}")
	public ResponseEntity<ResponseStructure<List<Expense>>> getYearlyReport(@PathVariable Long userId,
			@PathVariable int year) {
		return reportService.getYearlyReport(userId, year);
	}

	@GetMapping("/category/{userId}/{category}")
	public ResponseEntity<ResponseStructure<List<Expense>>> getCategoryReport(@PathVariable Long userId,
			@PathVariable String category) {
		return reportService.getCategoryReport(userId, category);
	}
}