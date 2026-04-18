package com.expensetracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.Expense;
import com.expensetracker.service.ExpenseService;

@RestController
@RequestMapping("/expense")
public class ExpenseController {

	@Autowired
	private ExpenseService expenseService;

	@PostMapping("/save/{userId}")
	public ResponseEntity<ResponseStructure<Expense>> saveExpense(@RequestBody Expense expense,
			@PathVariable Long userId) {
		return expenseService.saveExpense(expense, userId);
	}

	// Get all expenses by user
	@GetMapping("/getAll/{userId}")
	public ResponseEntity<ResponseStructure<List<Expense>>> getAllExpenseByUser(@PathVariable Long userId) {
		return expenseService.getAllExpenseByUser(userId);
	}

	// Get single expense
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseStructure<Expense>> getExpenseById(@PathVariable Long id) {
		return expenseService.getExpenseById(id);
	}

	// Update expense
	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseStructure<Expense>> updateExpense(@PathVariable Long id,
			@RequestBody Expense expense) {
		return expenseService.updateExpense(id, expense);
	}

	// Delete expense
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseStructure<String>> deleteExpense(@PathVariable Long id) {
		return expenseService.deleteExpense(id);
	}
}