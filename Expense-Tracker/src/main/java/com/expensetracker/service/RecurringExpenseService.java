package com.expensetracker.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.expensetracker.configuration.SecurityUtils;
import com.expensetracker.dao.ExpenseDao;
import com.expensetracker.dao.RecurringExpenseDao;
import com.expensetracker.dao.UserDao;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.RecurringExpense;
import com.expensetracker.entity.User;
import com.expensetracker.enums.Roles;
import com.expensetracker.exception.ExpenseNotFoundException;
import com.expensetracker.exception.UnauthorizedAccessException;
import com.expensetracker.exception.UserNotFoundException;

@Service
public class RecurringExpenseService {

	@Autowired
	private RecurringExpenseDao recurringExpenseDao;
	@Autowired
	private ExpenseDao expenseDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private SecurityUtils securityUtils;

	private void checkAccess(User caller, Long targetUserId) {
		if (caller.getRole() != Roles.ADMIN && !caller.getId().equals(targetUserId))
			throw new UnauthorizedAccessException("Access denied: you can only manage your own recurring expenses");
	}

	public ResponseEntity<ResponseStructure<RecurringExpense>> create(RecurringExpense recurring, Long userId) {
		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, userId);

		User user = userDao.getUserById(userId);
		if (user == null)
			throw new UserNotFoundException("User not found with id: " + userId);

		recurring.setUser(user);
		if (recurring.getNextDueDate() == null)
			recurring.setNextDueDate(recurring.getStartDate());

		ResponseStructure<RecurringExpense> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Recurring expense created successfully");
		response.setData(recurringExpenseDao.save(recurring));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	public ResponseEntity<ResponseStructure<List<RecurringExpense>>> getAllByUser(Long userId) {
		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, userId);
		if (userDao.getUserById(userId) == null)
			throw new UserNotFoundException("User not found with id: " + userId);

		ResponseStructure<List<RecurringExpense>> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Recurring expenses fetched successfully");
		response.setData(recurringExpenseDao.getAllByUser(userId));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<List<RecurringExpense>>> getActiveByUser(Long userId) {
		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, userId);
		if (userDao.getUserById(userId) == null)
			throw new UserNotFoundException("User not found with id: " + userId);

		ResponseStructure<List<RecurringExpense>> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Active recurring expenses fetched successfully");
		response.setData(recurringExpenseDao.getActiveByUser(userId));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<RecurringExpense>> update(Long id, RecurringExpense updated) {
		RecurringExpense existing = recurringExpenseDao.getById(id);
		if (existing == null)
			throw new ExpenseNotFoundException("Recurring expense not found with id: " + id);

		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, existing.getUser().getId());

		existing.setTitle(updated.getTitle());
		existing.setDescription(updated.getDescription());
		existing.setAmount(updated.getAmount());
		existing.setCategory(updated.getCategory());
		existing.setPaymentMethod(updated.getPaymentMethod());
		existing.setFrequency(updated.getFrequency());
		existing.setStartDate(updated.getStartDate());
		existing.setEndDate(updated.getEndDate());
		existing.setTime(updated.getTime());

		ResponseStructure<RecurringExpense> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Recurring expense updated successfully");
		response.setData(recurringExpenseDao.save(existing));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<RecurringExpense>> pause(Long id) {
		RecurringExpense existing = recurringExpenseDao.getById(id);
		if (existing == null)
			throw new ExpenseNotFoundException("Recurring expense not found with id: " + id);

		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, existing.getUser().getId());
		existing.setActive(false);

		ResponseStructure<RecurringExpense> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Recurring expense paused successfully");
		response.setData(recurringExpenseDao.save(existing));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<RecurringExpense>> resume(Long id) {
		RecurringExpense existing = recurringExpenseDao.getById(id);
		if (existing == null)
			throw new ExpenseNotFoundException("Recurring expense not found with id: " + id);

		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, existing.getUser().getId());
		existing.setActive(true);

		ResponseStructure<RecurringExpense> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Recurring expense resumed successfully");
		response.setData(recurringExpenseDao.save(existing));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<String>> delete(Long id) {
		RecurringExpense existing = recurringExpenseDao.getById(id);
		if (existing == null)
			throw new ExpenseNotFoundException("Recurring expense not found with id: " + id);

		User caller = securityUtils.getCurrentUser();
		checkAccess(caller, existing.getUser().getId());
		recurringExpenseDao.delete(id);

		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Recurring expense deleted successfully");
		response.setData("Deleted recurring expense with id: " + id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Scheduled(cron = "0 0 0 * * *")
	public void processRecurringExpenses() {
		LocalDate today = LocalDate.now();
		List<RecurringExpense> dueList = recurringExpenseDao.getDueRecurring(today);

		for (RecurringExpense recurring : dueList) {
			if (recurring.getEndDate() != null && today.isAfter(recurring.getEndDate())) {
				recurring.setActive(false);
				recurringExpenseDao.save(recurring);
				continue;
			}

			Expense expense = new Expense();
			expense.setTitle(recurring.getTitle());
			expense.setDescription(recurring.getDescription());
			expense.setAmount(recurring.getAmount());
			expense.setDate(recurring.getNextDueDate());
			expense.setTime(recurring.getTime());
			expense.setCategory(recurring.getCategory());
			expense.setPaymentMethod(recurring.getPaymentMethod());
			expense.setUser(recurring.getUser());
			expenseDao.saveExpense(expense);

			recurring.setNextDueDate(advanceDate(recurring.getNextDueDate(), recurring));
			recurringExpenseDao.save(recurring);
		}
	}

	private LocalDate advanceDate(LocalDate current, RecurringExpense recurring) {
		switch (recurring.getFrequency()) {
		case DAILY:
			return current.plusDays(1);
		case WEEKLY:
			return current.plusWeeks(1);
		case MONTHLY:
			return current.plusMonths(1);
		case YEARLY:
			return current.plusYears(1);
		default:
			return current.plusMonths(1);
		}
	}
}