package com.expensetracker.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.expensetracker.entity.RecurringExpense;
import com.expensetracker.repository.RecurringExpenseRepository;

@Repository
public class RecurringExpenseDao {

	@Autowired
	private RecurringExpenseRepository recurringExpenseRepository;

	public RecurringExpense save(RecurringExpense recurringExpense) {
		return recurringExpenseRepository.save(recurringExpense);
	}

	public RecurringExpense getById(Long id) {
		return recurringExpenseRepository.findById(id).orElse(null);
	}

	public List<RecurringExpense> getAllByUser(Long userId) {
		return recurringExpenseRepository.findByUserId(userId);
	}

	public List<RecurringExpense> getActiveByUser(Long userId) {
		return recurringExpenseRepository.findByUserIdAndActive(userId, true);
	}

	public List<RecurringExpense> getDueRecurring(LocalDate today) {
		return recurringExpenseRepository.findDueRecurring(today);
	}

	public void delete(Long id) {
		recurringExpenseRepository.deleteById(id);
	}
}