package com.expensetracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.expensetracker.dao.ExpenseDao;
import com.expensetracker.dao.UserDao;
import com.expensetracker.dto.PageResponse;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ExpenseNotFoundException;
import com.expensetracker.exception.UserNotFoundException;

@Service
public class ExpenseService {

	@Autowired
	private ExpenseDao expenseDao;

	@Autowired
	private UserDao userDao;

	public ResponseEntity<ResponseStructure<Expense>> saveExpense(Expense expense, Long userId) {
		ResponseStructure<Expense> response = new ResponseStructure<>();

		User user = userDao.getUserById(userId);

		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}

		expense.setUser(user);
		Expense saved = expenseDao.saveExpense(expense);

		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Expense added successfully");
		response.setData(saved);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Get all expenses by user
	public ResponseEntity<ResponseStructure<List<Expense>>> getAllExpenseByUser(Long userId) {
		ResponseStructure<List<Expense>> response = new ResponseStructure<>();

		User user = userDao.getUserById(userId);

		if (user == null) {
			response.setStatusCode(HttpStatus.NOT_FOUND.value());
			response.setMessage("User not found");
			response.setData(null);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		List<Expense> expenses = expenseDao.getAllExpenseByUser(userId);

		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expenses fetched successfully");
		response.setData(expenses);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Get single expense by ID
	public ResponseEntity<ResponseStructure<Expense>> getExpenseById(Long id) {
		ResponseStructure<Expense> response = new ResponseStructure<>();

		Expense expense = expenseDao.getExpenseById(id);

		if (expense == null) {
			throw new ExpenseNotFoundException("Expense not found with id: " + id);
		}

		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expense fetched successfully");
		response.setData(expense);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Update expense
	public ResponseEntity<ResponseStructure<Expense>> updateExpense(Long id, Expense updatedExpense) {
		ResponseStructure<Expense> response = new ResponseStructure<>();

		Expense existing = expenseDao.getExpenseById(id);

		if (existing == null) {
			throw new ExpenseNotFoundException("Expense not found with id: " + id);
		}

		// Update fields
		existing.setTitle(updatedExpense.getTitle());
		existing.setDescription(updatedExpense.getDescription());
		existing.setAmount(updatedExpense.getAmount());
		existing.setDate(updatedExpense.getDate());
		existing.setTime(updatedExpense.getTime());
		existing.setCategory(updatedExpense.getCategory());
		existing.setPaymentMethod(updatedExpense.getPaymentMethod());

		Expense saved = expenseDao.updateExpense(existing);

		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expense updated successfully");
		response.setData(saved);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Delete expense
	public ResponseEntity<ResponseStructure<String>> deleteExpense(Long id) {
		ResponseStructure<String> response = new ResponseStructure<>();

		Expense existing = expenseDao.getExpenseById(id);

		if (existing == null) {
			throw new ExpenseNotFoundException("Expense not found with id: " + id);
		}

		expenseDao.deleteExpense(id);

		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expense deleted successfully");
		response.setData("Deleted");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<PageResponse<Expense>>> getAllExpensePaginated(Long userId, int page,
			int size, String sortBy, String sortDir) {

		ResponseStructure<PageResponse<Expense>> response = new ResponseStructure<>();

		// Validate user exists
		User user = userDao.getUserById(userId);
		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}

		Page<Expense> expensePage = expenseDao.getAllExpenseByUserPaginated(userId, page, size, sortBy, sortDir);

		// Build PageResponse
		PageResponse<Expense> pageResponse = new PageResponse<>();
		pageResponse.setContent(expensePage.getContent());
		pageResponse.setCurrentPage(expensePage.getNumber());
		pageResponse.setTotalPages(expensePage.getTotalPages());
		pageResponse.setTotalElements(expensePage.getTotalElements());
		pageResponse.setPageSize(expensePage.getSize());
		pageResponse.setFirst(expensePage.isFirst());
		pageResponse.setLast(expensePage.isLast());

		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expenses fetched successfully");
		response.setData(pageResponse);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}