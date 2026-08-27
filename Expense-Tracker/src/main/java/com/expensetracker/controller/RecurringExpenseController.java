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
import com.expensetracker.entity.RecurringExpense;
import com.expensetracker.service.RecurringExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/recurring")
public class RecurringExpenseController {

	@Autowired
	private RecurringExpenseService recurringExpenseService;

	@PostMapping("/save/{userId}")
	public ResponseEntity<ResponseStructure<RecurringExpense>> create(@Valid @RequestBody RecurringExpense recurring,
			@PathVariable Long userId) {
		return recurringExpenseService.create(recurring, userId);
	}

	@GetMapping("/getAll/{userId}")
	public ResponseEntity<ResponseStructure<List<RecurringExpense>>> getAllByUser(@PathVariable Long userId) {
		return recurringExpenseService.getAllByUser(userId);
	}

	@GetMapping("/getActive/{userId}")
	public ResponseEntity<ResponseStructure<List<RecurringExpense>>> getActiveByUser(@PathVariable Long userId) {
		return recurringExpenseService.getActiveByUser(userId);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseStructure<RecurringExpense>> update(@PathVariable Long id,
			@Valid @RequestBody RecurringExpense recurring) {
		return recurringExpenseService.update(id, recurring);
	}

	@PutMapping("/pause/{id}")
	public ResponseEntity<ResponseStructure<RecurringExpense>> pause(@PathVariable Long id) {
		return recurringExpenseService.pause(id);
	}

	@PutMapping("/resume/{id}")
	public ResponseEntity<ResponseStructure<RecurringExpense>> resume(@PathVariable Long id) {
		return recurringExpenseService.resume(id);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseStructure<String>> delete(@PathVariable Long id) {
		return recurringExpenseService.delete(id);
	}
}