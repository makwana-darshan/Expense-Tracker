package com.expensetracker.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.expensetracker.dao.ExpenseDao;
import com.expensetracker.dao.UserDao;
import com.expensetracker.dto.DateRangeResponse;
import com.expensetracker.dto.PageResponse;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.enums.Roles;
import com.expensetracker.exception.ExpenseNotFoundException;
import com.expensetracker.exception.InvalidDateRangeException;
import com.expensetracker.exception.UnauthorizedAccessException;
import com.expensetracker.exception.UserNotFoundException;

@Service
public class ExpenseService {

	@Autowired
	private ExpenseDao expenseDao;

	@Autowired
	private UserDao userDao;

	// ─── Helper: verify requester has access to the target userId ─────────────
	private void checkAccess(Long requesterId, Long targetUserId) {
		User requester = userDao.getUserById(requesterId);
		if (requester == null) {
			throw new UserNotFoundException("Requesting user not found with id: " + requesterId);
		}
		if (requester.getRole() != Roles.ADMIN && !requesterId.equals(targetUserId)) {
			throw new UnauthorizedAccessException("Access denied: you can only access your own expenses");
		}
	}

	// ─── Helper: validate date range ──────────────────────────────────────────
	private void validateDateRange(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null) {
			throw new InvalidDateRangeException("startDate and endDate are required");
		}
		if (startDate.isAfter(endDate)) {
			throw new InvalidDateRangeException(
					"startDate (" + startDate + ") must not be after endDate (" + endDate + ")");
		}
		if (startDate.isAfter(LocalDate.now())) {
			throw new InvalidDateRangeException("startDate cannot be in the future");
		}
	}

	// ─── Save Expense ──────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<Expense>> saveExpense(Expense expense, Long userId, Long requesterId) {
		checkAccess(requesterId, userId);

		User user = userDao.getUserById(userId);
		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}

		expense.setUser(user);
		Expense saved = expenseDao.saveExpense(expense);

		ResponseStructure<Expense> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Expense added successfully");
		response.setData(saved);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// ─── Get All Expenses By User ──────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<List<Expense>>> getAllExpenseByUser(Long userId, Long requesterId) {
		checkAccess(requesterId, userId);

		User user = userDao.getUserById(userId);
		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}

		List<Expense> expenses = expenseDao.getAllExpenseByUser(userId);

		ResponseStructure<List<Expense>> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expenses fetched successfully");
		response.setData(expenses);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Get All Expenses (ADMIN only) ─────────────────────────────────────────
	public ResponseEntity<ResponseStructure<List<Expense>>> getAllExpenses(Long requesterId) {
		User requester = userDao.getUserById(requesterId);
		if (requester == null) {
			throw new UserNotFoundException("Requesting user not found with id: " + requesterId);
		}
		if (requester.getRole() != Roles.ADMIN) {
			throw new UnauthorizedAccessException("Access denied: only ADMIN can view all expenses");
		}

		List<Expense> expenses = expenseDao.getAllExpenses();

		ResponseStructure<List<Expense>> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("All expenses fetched successfully");
		response.setData(expenses);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Search by Date Range — single user ───────────────────────────────────
	public ResponseEntity<ResponseStructure<DateRangeResponse>> searchByDateRange(Long userId, LocalDate startDate,
			LocalDate endDate, Long requesterId) {

		checkAccess(requesterId, userId);

		User user = userDao.getUserById(userId);
		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}

		validateDateRange(startDate, endDate);

		List<Expense> expenses = expenseDao.getExpenseByDateRange(userId, startDate, endDate);
		BigDecimal total = expenseDao.getTotalByDateRange(userId, startDate, endDate);
		DateRangeResponse rangeResponse = new DateRangeResponse(startDate, endDate, expenses, total);

		ResponseStructure<DateRangeResponse> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expenses fetched for date range " + startDate + " to " + endDate);
		response.setData(rangeResponse);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Search by Date Range — ADMIN all users ───────────────────────────────
	public ResponseEntity<ResponseStructure<DateRangeResponse>> searchByDateRangeAllUsers(LocalDate startDate,
			LocalDate endDate, Long requesterId) {

		User requester = userDao.getUserById(requesterId);
		if (requester == null) {
			throw new UserNotFoundException("Requesting user not found with id: " + requesterId);
		}
		if (requester.getRole() != Roles.ADMIN) {
			throw new UnauthorizedAccessException("Access denied: only ADMIN can search all users' expenses");
		}

		validateDateRange(startDate, endDate);

		List<Expense> expenses = expenseDao.getExpenseByDateRangeAllUsers(startDate, endDate);
		BigDecimal total = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		DateRangeResponse rangeResponse = new DateRangeResponse(startDate, endDate, expenses, total);

		ResponseStructure<DateRangeResponse> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("All users' expenses fetched for date range " + startDate + " to " + endDate);
		response.setData(rangeResponse);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Get Single Expense By ID ──────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<Expense>> getExpenseById(Long id, Long requesterId) {
		Expense expense = expenseDao.getExpenseById(id);
		if (expense == null) {
			throw new ExpenseNotFoundException("Expense not found with id: " + id);
		}

		checkAccess(requesterId, expense.getUser().getId());

		ResponseStructure<Expense> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expense fetched successfully");
		response.setData(expense);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Update Expense ────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<Expense>> updateExpense(Long id, Expense updatedExpense, Long requesterId) {
		Expense existing = expenseDao.getExpenseById(id);
		if (existing == null) {
			throw new ExpenseNotFoundException("Expense not found with id: " + id);
		}

		checkAccess(requesterId, existing.getUser().getId());

		existing.setTitle(updatedExpense.getTitle());
		existing.setDescription(updatedExpense.getDescription());
		existing.setAmount(updatedExpense.getAmount());
		existing.setDate(updatedExpense.getDate());
		existing.setTime(updatedExpense.getTime());
		existing.setCategory(updatedExpense.getCategory());
		existing.setPaymentMethod(updatedExpense.getPaymentMethod());

		Expense saved = expenseDao.updateExpense(existing);

		ResponseStructure<Expense> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expense updated successfully");
		response.setData(saved);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Delete Expense ────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<String>> deleteExpense(Long id, Long requesterId) {
		Expense existing = expenseDao.getExpenseById(id);
		if (existing == null) {
			throw new ExpenseNotFoundException("Expense not found with id: " + id);
		}

		checkAccess(requesterId, existing.getUser().getId());
		expenseDao.deleteExpense(id);

		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expense deleted successfully");
		response.setData("Deleted expense with id: " + id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Get Paginated Expenses by User ───────────────────────────────────────
	public ResponseEntity<ResponseStructure<PageResponse<Expense>>> getAllExpensePaginated(Long userId, int page,
			int size, String sortBy, String sortDir, Long requesterId) {

		checkAccess(requesterId, userId);

		User user = userDao.getUserById(userId);
		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}

		Page<Expense> expensePage = expenseDao.getAllExpenseByUserPaginated(userId, page, size, sortBy, sortDir);

		PageResponse<Expense> pageResponse = new PageResponse<>();
		pageResponse.setContent(expensePage.getContent());
		pageResponse.setCurrentPage(expensePage.getNumber());
		pageResponse.setTotalPages(expensePage.getTotalPages());
		pageResponse.setTotalElements(expensePage.getTotalElements());
		pageResponse.setPageSize(expensePage.getSize());
		pageResponse.setFirst(expensePage.isFirst());
		pageResponse.setLast(expensePage.isLast());

		ResponseStructure<PageResponse<Expense>> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expenses fetched successfully");
		response.setData(pageResponse);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}