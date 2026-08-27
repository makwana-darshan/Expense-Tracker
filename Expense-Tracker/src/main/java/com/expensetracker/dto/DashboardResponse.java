package com.expensetracker.dto;

import java.math.BigDecimal;
import java.util.List;

import com.expensetracker.entity.Expense;

public class DashboardResponse {

	private BigDecimal totalAllTime;
	private BigDecimal totalThisMonth;
	private BigDecimal totalThisWeek;
	private BigDecimal avgPerDay;
	private int totalExpenseCount;

	private List<CategoryStat> categoryBreakdown;
	private List<MonthlyTrend> monthlyTrend;
	private List<DailyTrend> dailyTrend;
	private List<Expense> top5Expenses;

	public DashboardResponse() {
	}

	public BigDecimal getTotalAllTime() {
		return totalAllTime;
	}

	public void setTotalAllTime(BigDecimal totalAllTime) {
		this.totalAllTime = totalAllTime;
	}

	public BigDecimal getTotalThisMonth() {
		return totalThisMonth;
	}

	public void setTotalThisMonth(BigDecimal totalThisMonth) {
		this.totalThisMonth = totalThisMonth;
	}

	public BigDecimal getTotalThisWeek() {
		return totalThisWeek;
	}

	public void setTotalThisWeek(BigDecimal totalThisWeek) {
		this.totalThisWeek = totalThisWeek;
	}

	public BigDecimal getAvgPerDay() {
		return avgPerDay;
	}

	public void setAvgPerDay(BigDecimal avgPerDay) {
		this.avgPerDay = avgPerDay;
	}

	public int getTotalExpenseCount() {
		return totalExpenseCount;
	}

	public void setTotalExpenseCount(int totalExpenseCount) {
		this.totalExpenseCount = totalExpenseCount;
	}

	public List<CategoryStat> getCategoryBreakdown() {
		return categoryBreakdown;
	}

	public void setCategoryBreakdown(List<CategoryStat> categoryBreakdown) {
		this.categoryBreakdown = categoryBreakdown;
	}

	public List<MonthlyTrend> getMonthlyTrend() {
		return monthlyTrend;
	}

	public void setMonthlyTrend(List<MonthlyTrend> monthlyTrend) {
		this.monthlyTrend = monthlyTrend;
	}

	public List<DailyTrend> getDailyTrend() {
		return dailyTrend;
	}

	public void setDailyTrend(List<DailyTrend> dailyTrend) {
		this.dailyTrend = dailyTrend;
	}

	public List<Expense> getTop5Expenses() {
		return top5Expenses;
	}

	public void setTop5Expenses(List<Expense> top5Expenses) {
		this.top5Expenses = top5Expenses;
	}
}