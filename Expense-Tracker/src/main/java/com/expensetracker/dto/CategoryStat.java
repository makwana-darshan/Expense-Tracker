package com.expensetracker.dto;

import java.math.BigDecimal;

public class CategoryStat {

	private String category;
	private BigDecimal totalAmount;
	private int count;
	private double percentage;

	public CategoryStat() {
	}

	public CategoryStat(String category, BigDecimal totalAmount, int count) {
		this.category = category;
		this.totalAmount = totalAmount;
		this.count = count;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
}