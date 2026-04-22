package com.expensetracker.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.dto.DateRangeResponse;
import com.expensetracker.dto.PageResponse;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.Expense;
import com.expensetracker.service.ExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

	@Autowired
	private ExpenseService expenseService;

	@PostMapping("/save/{userId}")
	public ResponseEntity<ResponseStructure<Expense>> saveExpense(@Valid @RequestBody Expense expense,
			@PathVariable Long userId) {
		return expenseService.saveExpense(expense, userId);
	}

	@GetMapping("/getAll/{userId}")
	public ResponseEntity<ResponseStructure<List<Expense>>> getAllExpenseByUser(@PathVariable Long userId) {
		return expenseService.getAllExpenseByUser(userId);
	}

	@GetMapping("/getAll")
	public ResponseEntity<ResponseStructure<List<Expense>>> getAllExpenses() {
		return expenseService.getAllExpenses();
	}

	@GetMapping("/getAll/{userId}/paginated")
	public ResponseEntity<ResponseStructure<PageResponse<Expense>>> getAllExpensePaginated(@PathVariable Long userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "date") String sortBy, @RequestParam(defaultValue = "desc") String sortDir) {
		return expenseService.getAllExpensePaginated(userId, page, size, sortBy, sortDir);
	}

	@GetMapping("/search/{userId}")
	public ResponseEntity<ResponseStructure<DateRangeResponse>> searchByDateRange(@PathVariable Long userId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return expenseService.searchByDateRange(userId, startDate, endDate);
	}

	@GetMapping("/search")
	public ResponseEntity<ResponseStructure<DateRangeResponse>> searchByDateRangeAllUsers(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		return expenseService.searchByDateRangeAllUsers(startDate, endDate);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseStructure<Expense>> getExpenseById(@PathVariable Long id) {
		return expenseService.getExpenseById(id);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseStructure<Expense>> updateExpense(@Valid @PathVariable Long id,
			@RequestBody Expense expense) {
		return expenseService.updateExpense(id, expense);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseStructure<String>> deleteExpense(@PathVariable Long id) {
		return expenseService.deleteExpense(id);
	}
}