package com.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DailyTrend {

	private LocalDate date;
	private BigDecimal totalAmount;
	private int count;

	public DailyTrend() {
	}

	public DailyTrend(LocalDate date, BigDecimal totalAmount, int count) {
		this.date = date;
		this.totalAmount = totalAmount;
		this.count = count;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}