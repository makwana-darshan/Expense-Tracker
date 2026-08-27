package com.expensetracker.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.expensetracker.dao.UserDao;
import com.expensetracker.dto.ForgotPasswordRequest;
import com.expensetracker.dto.ResetPasswordRequest;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.PasswordResetToken;
import com.expensetracker.entity.User;
import com.expensetracker.exception.InvalidTokenException;
import com.expensetracker.exception.TokenExpiredException;
import com.expensetracker.repository.PasswordResetTokenRepository;

@Service
public class PasswordResetService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordResetTokenRepository tokenRepository;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${app.mail.from}")
	private String fromEmail;

	@Value("${app.frontend.url:http://localhost:8080}")
	private String frontendUrl;

	private static final int TOKEN_EXPIRY_MINUTES = 15;

	// ─── Step 1: Send reset email ─────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<String>> forgotPassword(ForgotPasswordRequest request) {
		User user = userDao.findByEmail(request.getEmail());

		// Never reveal whether email exists — always return same message
		if (user == null) {
			return buildSuccess("If this email is registered, a reset link has been sent.");
		}

		// Invalidate any previous tokens for this user
		tokenRepository.deleteAllByUserId(user.getId());

		String tokenValue = UUID.randomUUID().toString();

		PasswordResetToken token = new PasswordResetToken();
		token.setToken(tokenValue);
		token.setUser(user);
		token.setExpiryTime(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES));
		token.setUsed(false);
		tokenRepository.save(token);

		sendResetEmail(user, tokenValue);

		return buildSuccess("If this email is registered, a reset link has been sent.");
	}

	// ─── Step 2: Validate token and reset password ────────────────────────────
	public ResponseEntity<ResponseStructure<String>> resetPassword(ResetPasswordRequest request) {
		PasswordResetToken token = tokenRepository.findByToken(request.getToken())
				.orElseThrow(() -> new InvalidTokenException(
						"Invalid or unrecognized reset token. Please request a new one."));

		if (token.getUsed()) {
			throw new InvalidTokenException(
					"This reset link has already been used. Please request a new one.");
		}

		if (LocalDateTime.now().isAfter(token.getExpiryTime())) {
			throw new TokenExpiredException(
					"This reset link has expired (valid for " + TOKEN_EXPIRY_MINUTES
							+ " minutes). Please request a new one.");
		}

		User user = token.getUser();
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userDao.saveUser(user);

		token.setUsed(true);
		tokenRepository.save(token);

		return buildSuccess("Password has been reset successfully. You can now log in.");
	}

	// ─── Email ────────────────────────────────────────────────────────────────
	private void sendResetEmail(User user, String tokenValue) {
		String resetLink = frontendUrl + "/reset-password?token=" + tokenValue;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(user.getEmail());
		message.setSubject("Expense Tracker — Password Reset Request");
		message.setText(
				"Hi " + user.getFullName() + ",\n\n"
				+ "We received a request to reset your password.\n\n"
				+ "Click the link below to reset it (valid for " + TOKEN_EXPIRY_MINUTES + " minutes):\n"
				+ resetLink + "\n\n"
				+ "If you did not request this, please ignore this email.\n\n"
				+ "— Expense Tracker Team");

		mailSender.send(message);
	}

	private ResponseEntity<ResponseStructure<String>> buildSuccess(String message) {
		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage(message);
		response.setData(null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}