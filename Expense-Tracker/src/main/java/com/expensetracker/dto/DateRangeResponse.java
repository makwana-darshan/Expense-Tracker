package com.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.expensetracker.entity.Expense;

public class DateRangeResponse {

	private LocalDate startDate;
	private LocalDate endDate;
	private int totalDays;
	private int totalExpenses;
	private BigDecimal totalAmount;
	private List<Expense> expenses;

	public DateRangeResponse() {
	}

	public DateRangeResponse(LocalDate startDate, LocalDate endDate, List<Expense> expenses,
			BigDecimal totalAmount) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.totalDays = (int) (endDate.toEpochDay() - startDate.toEpochDay()) + 1;
		this.expenses = expenses;
		this.totalExpenses = expenses.size();
		this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
	}

	public LocalDate getStartDate() { return startDate; }
	public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

	public LocalDate getEndDate() { return endDate; }
	public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

	public int getTotalDays() { return totalDays; }
	public void setTotalDays(int totalDays) { this.totalDays = totalDays; }

	public int getTotalExpenses() { return totalExpenses; }
	public void setTotalExpenses(int totalExpenses) { this.totalExpenses = totalExpenses; }

	public BigDecimal getTotalAmount() { return totalAmount; }
	public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

	public List<Expense> getExpenses() { return expenses; }
	public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }
}