package com.expensetracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.dto.ForgotPasswordRequest;
import com.expensetracker.dto.ResetPasswordRequest;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.service.PasswordResetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class PasswordResetController {

	@Autowired
	private PasswordResetService passwordResetService;

	// POST /user/forgot-password
	// Body: { "email": "user@example.com" }
	@PostMapping("/forgot-password")
	public ResponseEntity<ResponseStructure<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
		return passwordResetService.forgotPassword(request);
	}

	// POST /user/reset-password
	// Body: { "token": "uuid-here", "newPassword": "newPass123" }
	@PostMapping("/reset-password")
	public ResponseEntity<ResponseStructure<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		return passwordResetService.resetPassword(request);
	}
}