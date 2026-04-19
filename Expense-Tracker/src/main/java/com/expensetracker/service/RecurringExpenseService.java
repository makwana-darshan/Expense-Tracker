package com.expensetracker.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

	// ─── Helper ───────────────────────────────────────────────────────────────
	private void checkAccess(Long requesterId, Long targetUserId) {
		User requester = userDao.getUserById(requesterId);
		if (requester == null) {
			throw new UserNotFoundException("Requesting user not found with id: " + requesterId);
		}
		if (requester.getRole() != Roles.ADMIN && !requesterId.equals(targetUserId)) {
			throw new UnauthorizedAccessException(
					"Access denied: you can only manage your own recurring expenses");
		}
	}

	// ─── Create ───────────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<RecurringExpense>> create(RecurringExpense recurring,
			Long userId, Long requesterId) {
		checkAccess(requesterId, userId);

		User user = userDao.getUserById(userId);
		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}

		recurring.setUser(user);
		if (recurring.getNextDueDate() == null) {
			recurring.setNextDueDate(recurring.getStartDate());
		}

		RecurringExpense saved = recurringExpenseDao.save(recurring);

		ResponseStructure<RecurringExpense> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("Recurring expense created successfully");
		response.setData(saved);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// ─── Get All ──────────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<List<RecurringExpense>>> getAllByUser(Long userId,
			Long requesterId) {
		checkAccess(requesterId, userId);

		if (userDao.getUserById(userId) == null) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}

		ResponseStructure<List<RecurringExpense>> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Recurring expenses fetched successfully");
		response.setData(recurringExpenseDao.getAllByUser(userId));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Get Active ───────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<List<RecurringExpense>>> getActiveByUser(Long userId,
			Long requesterId) {
		checkAccess(requesterId, userId);

		if (userDao.getUserById(userId) == null) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}

		ResponseStructure<List<RecurringExpense>> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Active recurring expenses fetched successfully");
		response.setData(recurringExpenseDao.getActiveByUser(userId));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Update ───────────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<RecurringExpense>> update(Long id,
			RecurringExpense updated, Long requesterId) {
		RecurringExpense existing = recurringExpenseDao.getById(id);
		if (existing == null) {
			throw new ExpenseNotFoundException("Recurring expense not found with id: " + id);
		}

		checkAccess(requesterId, existing.getUser().getId());

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

	// ─── Pause ────────────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<RecurringExpense>> pause(Long id, Long requesterId) {
		RecurringExpense existing = recurringExpenseDao.getById(id);
		if (existing == null) {
			throw new ExpenseNotFoundException("Recurring expense not found with id: " + id);
		}

		checkAccess(requesterId, existing.getUser().getId());
		existing.setActive(false);

		ResponseStructure<RecurringExpense> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Recurring expense paused successfully");
		response.setData(recurringExpenseDao.save(existing));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Resume ───────────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<RecurringExpense>> resume(Long id, Long requesterId) {
		RecurringExpense existing = recurringExpenseDao.getById(id);
		if (existing == null) {
			throw new ExpenseNotFoundException("Recurring expense not found with id: " + id);
		}

		checkAccess(requesterId, existing.getUser().getId());
		existing.setActive(true);

		ResponseStructure<RecurringExpense> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Recurring expense resumed successfully");
		response.setData(recurringExpenseDao.save(existing));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Delete ───────────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<String>> delete(Long id, Long requesterId) {
		RecurringExpense existing = recurringExpenseDao.getById(id);
		if (existing == null) {
			throw new ExpenseNotFoundException("Recurring expense not found with id: " + id);
		}

		checkAccess(requesterId, existing.getUser().getId());
		recurringExpenseDao.delete(id);

		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Recurring expense deleted successfully");
		response.setData("Deleted recurring expense with id: " + id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Scheduler: runs every day at midnight ────────────────────────────────
	@Scheduled(cron = "0 0 0 * * *")
	public void processRecurringExpenses() {
		LocalDate today = LocalDate.now();
		List<RecurringExpense> dueList = recurringExpenseDao.getDueRecurring(today);

		for (RecurringExpense recurring : dueList) {
			// Past end date — deactivate and skip
			if (recurring.getEndDate() != null && today.isAfter(recurring.getEndDate())) {
				recurring.setActive(false);
				recurringExpenseDao.save(recurring);
				continue;
			}

			// Create actual expense record
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

			// Advance next due date
			recurring.setNextDueDate(advanceDate(recurring.getNextDueDate(), recurring));
			recurringExpenseDao.save(recurring);
		}
	}

	private LocalDate advanceDate(LocalDate current, RecurringExpense recurring) {
		switch (recurring.getFrequency()) {
			case DAILY:   return current.plusDays(1);
			case WEEKLY:  return current.plusWeeks(1);
			case MONTHLY: return current.plusMonths(1);
			case YEARLY:  return current.plusYears(1);
			default:      return current.plusMonths(1);
		}
	}
}