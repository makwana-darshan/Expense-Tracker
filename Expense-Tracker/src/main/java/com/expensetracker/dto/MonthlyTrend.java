package com.expensetracker.dto;

import java.math.BigDecimal;

public class MonthlyTrend {

	private int month;
	private String monthName;
	private int year;
	private BigDecimal totalAmount;
	private int count;

	public MonthlyTrend() {
	}

	public MonthlyTrend(int month, int year, BigDecimal totalAmount, int count) {
		this.month = month;
		this.year = year;
		this.totalAmount = totalAmount;
		this.count = count;
		this.monthName = java.time.Month.of(month).getDisplayName(java.time.format.TextStyle.FULL,
				java.util.Locale.ENGLISH);
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public String getMonthName() {
		return monthName;
	}

	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
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