package com.expensetracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.dto.DashboardResponse;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@GetMapping("/{userId}")
	public ResponseEntity<ResponseStructure<DashboardResponse>> getDashboard(@PathVariable Long userId,
			@RequestParam(defaultValue = "0") int year) {
		if (year == 0)
			year = java.time.LocalDate.now().getYear();
		return dashboardService.getDashboard(userId, year);
	}
}