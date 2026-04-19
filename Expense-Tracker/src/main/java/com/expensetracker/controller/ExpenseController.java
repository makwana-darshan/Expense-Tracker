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
			@PathVariable Long userId, @RequestParam Long requesterId) {
		return expenseService.saveExpense(expense, userId, requesterId);
	}

	@GetMapping("/getAll/{userId}")
	public ResponseEntity<ResponseStructure<List<Expense>>> getAllExpenseByUser(@PathVariable Long userId,
			@RequestParam Long requesterId) {
		return expenseService.getAllExpenseByUser(userId, requesterId);
	}

	@GetMapping("/getAll")
	public ResponseEntity<ResponseStructure<List<Expense>>> getAllExpenses(@RequestParam Long requesterId) {
		return expenseService.getAllExpenses(requesterId);
	}

	@GetMapping("/getAll/{userId}/paginated")
	public ResponseEntity<ResponseStructure<PageResponse<Expense>>> getAllExpensePaginated(@PathVariable Long userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "date") String sortBy, @RequestParam(defaultValue = "desc") String sortDir,
			@RequestParam Long requesterId) {
		return expenseService.getAllExpensePaginated(userId, page, size, sortBy, sortDir, requesterId);
	}

	// USER/ADMIN: search own expenses by date range
	// GET
	// /expense/search/{userId}?startDate=2025-01-01&endDate=2025-01-31&requesterId=1
	@GetMapping("/search/{userId}")
	public ResponseEntity<ResponseStructure<DateRangeResponse>> searchByDateRange(@PathVariable Long userId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam Long requesterId) {
		return expenseService.searchByDateRange(userId, startDate, endDate, requesterId);
	}

	// ADMIN only: search ALL users' expenses by date range
	// GET /expense/search?startDate=2025-01-01&endDate=2025-01-31&requesterId=1
	@GetMapping("/search")
	public ResponseEntity<ResponseStructure<DateRangeResponse>> searchByDateRangeAllUsers(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam Long requesterId) {
		return expenseService.searchByDateRangeAllUsers(startDate, endDate, requesterId);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseStructure<Expense>> getExpenseById(@PathVariable Long id,
			@RequestParam Long requesterId) {
		return expenseService.getExpenseById(id, requesterId);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseStructure<Expense>> updateExpense(@Valid @PathVariable Long id,
			@RequestBody Expense expense, @RequestParam Long requesterId) {
		return expenseService.updateExpense(id, expense, requesterId);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseStructure<String>> deleteExpense(@PathVariable Long id,
			@RequestParam Long requesterId) {
		return expenseService.deleteExpense(id, requesterId);
	}
}