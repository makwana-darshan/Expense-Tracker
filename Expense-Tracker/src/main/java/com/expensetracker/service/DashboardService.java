package com.expensetracker.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.expensetracker.dao.ExpenseDao;
import com.expensetracker.dao.UserDao;
import com.expensetracker.dto.CategoryStat;
import com.expensetracker.dto.DailyTrend;
import com.expensetracker.dto.DashboardResponse;
import com.expensetracker.dto.MonthlyTrend;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.enums.Roles;
import com.expensetracker.exception.UnauthorizedAccessException;
import com.expensetracker.exception.UserNotFoundException;

@Service
public class DashboardService {

	@Autowired
	private ExpenseDao expenseDao;

	@Autowired
	private UserDao userDao;

	public ResponseEntity<ResponseStructure<DashboardResponse>> getDashboard(Long userId, int year, Long requesterId) {

		User requester = userDao.getUserById(requesterId);
		if (requester == null) {
			throw new UserNotFoundException("Requesting user not found with id: " + requesterId);
		}
		if (requester.getRole() != Roles.ADMIN && !requesterId.equals(userId)) {
			throw new UnauthorizedAccessException("Access denied: you can only view your own dashboard");
		}

		if (userDao.getUserById(userId) == null) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}

		LocalDate today = LocalDate.now();
		int currentMonth = today.getMonthValue();
		int currentYear = today.getYear();

		DashboardResponse dashboard = new DashboardResponse();

		// ── Summary cards ──────────────────────────────────────────────────────
		BigDecimal totalAllTime = expenseDao.getTotalExpenseByUser(userId);
		dashboard.setTotalAllTime(totalAllTime);

		LocalDate monthStart = LocalDate.of(currentYear, currentMonth, 1);
		LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
		BigDecimal totalThisMonth = expenseDao.getTotalByDateRange(userId, monthStart, monthEnd);
		dashboard.setTotalThisMonth(totalThisMonth);

		LocalDate weekStart = today.minusDays(6);
		BigDecimal totalThisWeek = expenseDao.getTotalByDateRange(userId, weekStart, today);
		dashboard.setTotalThisWeek(totalThisWeek);

		int daysPassed = today.getDayOfMonth();
		BigDecimal avgPerDay = (totalThisMonth.compareTo(BigDecimal.ZERO) > 0 && daysPassed > 0)
				? totalThisMonth.divide(BigDecimal.valueOf(daysPassed), 2, RoundingMode.HALF_UP)
				: BigDecimal.ZERO;
		dashboard.setAvgPerDay(avgPerDay);

		dashboard.setTotalExpenseCount(expenseDao.countByUserId(userId));

		// ── Category breakdown (pie chart) ─────────────────────────────────────
		List<Object[]> categoryRows = expenseDao.getCategoryBreakdown(userId);
		List<CategoryStat> categoryStats = new ArrayList<>();
		for (Object[] row : categoryRows) {
			String category = row[0].toString();
			BigDecimal amount = (BigDecimal) row[1];
			int count = ((Number) row[2]).intValue();
			categoryStats.add(new CategoryStat(category, amount, count));
		}
		for (CategoryStat stat : categoryStats) {
			double pct = totalAllTime.compareTo(BigDecimal.ZERO) > 0 ? stat.getTotalAmount()
					.multiply(BigDecimal.valueOf(100)).divide(totalAllTime, 2, RoundingMode.HALF_UP).doubleValue()
					: 0.0;
			stat.setPercentage(pct);
		}
		dashboard.setCategoryBreakdown(categoryStats);

		// ── Monthly trend (bar/line chart) ─────────────────────────────────────
		List<Object[]> monthlyRows = expenseDao.getMonthlyTrend(userId, year);
		BigDecimal[] monthlyAmounts = new BigDecimal[13];
		int[] monthlyCounts = new int[13];
		for (int i = 1; i <= 12; i++) {
			monthlyAmounts[i] = BigDecimal.ZERO;
			monthlyCounts[i] = 0;
		}
		for (Object[] row : monthlyRows) {
			int month = ((Number) row[0]).intValue();
			monthlyAmounts[month] = (BigDecimal) row[1];
			monthlyCounts[month] = ((Number) row[2]).intValue();
		}
		List<MonthlyTrend> monthlyTrend = new ArrayList<>();
		for (int m = 1; m <= 12; m++) {
			monthlyTrend.add(new MonthlyTrend(m, year, monthlyAmounts[m], monthlyCounts[m]));
		}
		dashboard.setMonthlyTrend(monthlyTrend);

		// ── Daily trend (line chart — current month) ───────────────────────────
		List<Object[]> dailyRows = expenseDao.getDailyTrend(userId, currentMonth, currentYear);
		List<DailyTrend> dailyTrend = new ArrayList<>();
		for (Object[] row : dailyRows) {
			LocalDate date = (LocalDate) row[0];
			BigDecimal amount = (BigDecimal) row[1];
			int count = ((Number) row[2]).intValue();
			dailyTrend.add(new DailyTrend(date, amount, count));
		}
		dashboard.setDailyTrend(dailyTrend);

		// ── Top 5 expenses ─────────────────────────────────────────────────────
		dashboard.setTop5Expenses(expenseDao.getTop5Expenses(userId));

		ResponseStructure<DashboardResponse> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Dashboard data fetched successfully");
		response.setData(dashboard);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}