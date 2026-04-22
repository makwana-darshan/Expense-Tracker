package com.expensetracker.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.expensetracker.configuration.SecurityUtils;
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

	@Autowired
	private SecurityUtils securityUtils;

	private void checkAccess(User caller, Long targetUserId) {
		if (caller.getRole() != Roles.ADMIN && !caller.getId().equals(targetUserId)) {
			throw new UnauthorizedAccessException("Access denied: you can only access your own expenses");
		}
	}

	private void validateDateRange(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null)
			throw new InvalidDateRangeException("startDate and endDate are required");
		if (startDate.isAfter(endDate))
			throw new InvalidDateRangeException(
					"startDate (" + startDate + ") must not be after endDate (" + endDate + ")");
		if (startDate.isAfter(LocalDate.now()))
			throw new InvalidDateRangeException("startDate cannot be in the future");
	}

	public ResponseEntity<ResponseStructure<Expense>> saveExpense(Expense expense, Long userId) {
		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, userId);

		User user = userDao.getUserById(userId);
		if (user == null)
			throw new UserNotFoundException("User not found with id: " + userId);

		expense.setUser(user);
		ResponseStructure<Expense> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Expense added successfully");
		response.setData(expenseDao.saveExpense(expense));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	public ResponseEntity<ResponseStructure<List<Expense>>> getAllExpenseByUser(Long userId) {
		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, userId);

		if (userDao.getUserById(userId) == null)
			throw new UserNotFoundException("User not found with id: " + userId);

		ResponseStructure<List<Expense>> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expenses fetched successfully");
		response.setData(expenseDao.getAllExpenseByUser(userId));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<List<Expense>>> getAllExpenses() {
		User caller = securityUtils.getCurrentUser();
		if (caller.getRole() != Roles.ADMIN)
			throw new UnauthorizedAccessException("Access denied: only ADMIN can view all expenses");

		ResponseStructure<List<Expense>> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("All expenses fetched successfully");
		response.setData(expenseDao.getAllExpenses());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<DateRangeResponse>> searchByDateRange(Long userId, LocalDate startDate,
			LocalDate endDate) {
		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, userId);

		if (userDao.getUserById(userId) == null)
			throw new UserNotFoundException("User not found with id: " + userId);

		validateDateRange(startDate, endDate);

		List<Expense> expenses = expenseDao.getExpenseByDateRange(userId, startDate, endDate);
		BigDecimal total = expenseDao.getTotalByDateRange(userId, startDate, endDate);

		ResponseStructure<DateRangeResponse> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expenses fetched for " + startDate + " to " + endDate);
		response.setData(new DateRangeResponse(startDate, endDate, expenses, total));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<DateRangeResponse>> searchByDateRangeAllUsers(LocalDate startDate,
			LocalDate endDate) {
		User caller = securityUtils.getCurrentUser();
		if (caller.getRole() != Roles.ADMIN)
			throw new UnauthorizedAccessException("Access denied: only ADMIN can search all users' expenses");

		validateDateRange(startDate, endDate);

		List<Expense> expenses = expenseDao.getExpenseByDateRangeAllUsers(startDate, endDate);
		BigDecimal total = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

		ResponseStructure<DateRangeResponse> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("All users' expenses fetched for " + startDate + " to " + endDate);
		response.setData(new DateRangeResponse(startDate, endDate, expenses, total));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<Expense>> getExpenseById(Long id) {
		Expense expense = expenseDao.getExpenseById(id);
		if (expense == null)
			throw new ExpenseNotFoundException("Expense not found with id: " + id);

		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, expense.getUser().getId());

		ResponseStructure<Expense> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expense fetched successfully");
		response.setData(expense);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<Expense>> updateExpense(Long id, Expense updated) {
		Expense existing = expenseDao.getExpenseById(id);
		if (existing == null)
			throw new ExpenseNotFoundException("Expense not found with id: " + id);

		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, existing.getUser().getId());

		existing.setTitle(updated.getTitle());
		existing.setDescription(updated.getDescription());
		existing.setAmount(updated.getAmount());
		existing.setDate(updated.getDate());
		existing.setTime(updated.getTime());
		existing.setCategory(updated.getCategory());
		existing.setPaymentMethod(updated.getPaymentMethod());

		ResponseStructure<Expense> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expense updated successfully");
		response.setData(expenseDao.updateExpense(existing));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<String>> deleteExpense(Long id) {
		Expense existing = expenseDao.getExpenseById(id);
		if (existing == null)
			throw new ExpenseNotFoundException("Expense not found with id: " + id);

		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, existing.getUser().getId());
		expenseDao.deleteExpense(id);

		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Expense deleted successfully");
		response.setData("Deleted expense with id: " + id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<PageResponse<Expense>>> getAllExpensePaginated(Long userId, int page,
			int size, String sortBy, String sortDir) {
		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, userId);

		if (userDao.getUserById(userId) == null)
			throw new UserNotFoundException("User not found with id: " + userId);

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