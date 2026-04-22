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

import com.expensetracker.configuration.SecurityUtils;
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
	@Autowired
	private SecurityUtils securityUtils;

	public ResponseEntity<ResponseStructure<DashboardResponse>> getDashboard(Long userId, int year) {
		User caller = securityUtils.getCurrentUser();
		if (caller.getRole() != Roles.ADMIN && !caller.getId().equals(userId))
			throw new UnauthorizedAccessException("Access denied: you can only view your own dashboard");

		if (userDao.getUserById(userId) == null)
			throw new UserNotFoundException("User not found with id: " + userId);

		LocalDate today = LocalDate.now();
		int currentMonth = today.getMonthValue();
		int currentYear = today.getYear();

		DashboardResponse dashboard = new DashboardResponse();

		BigDecimal totalAllTime = expenseDao.getTotalExpenseByUser(userId);
		dashboard.setTotalAllTime(totalAllTime);

		LocalDate monthStart = LocalDate.of(currentYear, currentMonth, 1);
		LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());
		BigDecimal totalThisMonth = expenseDao.getTotalByDateRange(userId, monthStart, monthEnd);
		dashboard.setTotalThisMonth(totalThisMonth);
		dashboard.setTotalThisWeek(expenseDao.getTotalByDateRange(userId, today.minusDays(6), today));

		int daysPassed = today.getDayOfMonth();
		dashboard.setAvgPerDay(totalThisMonth.compareTo(BigDecimal.ZERO) > 0 && daysPassed > 0
				? totalThisMonth.divide(BigDecimal.valueOf(daysPassed), 2, RoundingMode.HALF_UP)
				: BigDecimal.ZERO);
		dashboard.setTotalExpenseCount(expenseDao.countByUserId(userId));

		List<CategoryStat> categoryStats = new ArrayList<>();
		for (Object[] row : expenseDao.getCategoryBreakdown(userId)) {
			CategoryStat stat = new CategoryStat(row[0].toString(), (BigDecimal) row[1], ((Number) row[2]).intValue());
			stat.setPercentage(totalAllTime.compareTo(BigDecimal.ZERO) > 0 ? stat.getTotalAmount()
					.multiply(BigDecimal.valueOf(100)).divide(totalAllTime, 2, RoundingMode.HALF_UP).doubleValue()
					: 0.0);
			categoryStats.add(stat);
		}
		dashboard.setCategoryBreakdown(categoryStats);

		BigDecimal[] amounts = new BigDecimal[13];
		int[] counts = new int[13];
		for (int i = 1; i <= 12; i++) {
			amounts[i] = BigDecimal.ZERO;
			counts[i] = 0;
		}
		for (Object[] row : expenseDao.getMonthlyTrend(userId, year)) {
			int m = ((Number) row[0]).intValue();
			amounts[m] = (BigDecimal) row[1];
			counts[m] = ((Number) row[2]).intValue();
		}
		List<MonthlyTrend> monthlyTrend = new ArrayList<>();
		for (int m = 1; m <= 12; m++)
			monthlyTrend.add(new MonthlyTrend(m, year, amounts[m], counts[m]));
		dashboard.setMonthlyTrend(monthlyTrend);

		List<DailyTrend> dailyTrend = new ArrayList<>();
		for (Object[] row : expenseDao.getDailyTrend(userId, currentMonth, currentYear))
			dailyTrend.add(new DailyTrend((LocalDate) row[0], (BigDecimal) row[1], ((Number) row[2]).intValue()));
		dashboard.setDailyTrend(dailyTrend);

		List<Expense> top5 = expenseDao.getTop5Expenses(userId);
		dashboard.setTop5Expenses(top5);

		ResponseStructure<DashboardResponse> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Dashboard data fetched successfully");
		response.setData(dashboard);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}