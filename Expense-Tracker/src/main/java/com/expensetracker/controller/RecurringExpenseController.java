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
import org.springframework.web.bind.annotation.RequestParam;
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

	// POST /recurring/save/{userId}?requesterId=1
	@PostMapping("/save/{userId}")
	public ResponseEntity<ResponseStructure<RecurringExpense>> create(@Valid @RequestBody RecurringExpense recurring,
			@PathVariable Long userId, @RequestParam Long requesterId) {
		return recurringExpenseService.create(recurring, userId, requesterId);
	}

	// GET /recurring/getAll/{userId}?requesterId=1
	@GetMapping("/getAll/{userId}")
	public ResponseEntity<ResponseStructure<List<RecurringExpense>>> getAllByUser(@PathVariable Long userId,
			@RequestParam Long requesterId) {
		return recurringExpenseService.getAllByUser(userId, requesterId);
	}

	// GET /recurring/getActive/{userId}?requesterId=1
	@GetMapping("/getActive/{userId}")
	public ResponseEntity<ResponseStructure<List<RecurringExpense>>> getActiveByUser(@PathVariable Long userId,
			@RequestParam Long requesterId) {
		return recurringExpenseService.getActiveByUser(userId, requesterId);
	}

	// PUT /recurring/update/{id}?requesterId=1
	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseStructure<RecurringExpense>> update(@PathVariable Long id,
			@Valid @RequestBody RecurringExpense recurring, @RequestParam Long requesterId) {
		return recurringExpenseService.update(id, recurring, requesterId);
	}

	// PUT /recurring/pause/{id}?requesterId=1
	@PutMapping("/pause/{id}")
	public ResponseEntity<ResponseStructure<RecurringExpense>> pause(@PathVariable Long id,
			@RequestParam Long requesterId) {
		return recurringExpenseService.pause(id, requesterId);
	}

	// PUT /recurring/resume/{id}?requesterId=1
	@PutMapping("/resume/{id}")
	public ResponseEntity<ResponseStructure<RecurringExpense>> resume(@PathVariable Long id,
			@RequestParam Long requesterId) {
		return recurringExpenseService.resume(id, requesterId);
	}

	// DELETE /recurring/delete/{id}?requesterId=1
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseStructure<String>> delete(@PathVariable Long id, @RequestParam Long requesterId) {
		return recurringExpenseService.delete(id, requesterId);
	}
}