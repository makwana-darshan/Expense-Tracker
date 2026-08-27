package com.expensetracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.expensetracker.dto.ResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// ─── Helper ────────────────────────────────────────────────────────────────
	private ResponseEntity<ResponseStructure<String>> buildError(HttpStatus status, String message,
			HttpServletRequest request) {
		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(status.value());
		response.setMessage(message);
		response.setData(null);
		if (request != null) {
			response.setPath(request.getRequestURI());
		}
		return new ResponseEntity<>(response, status);
	}

	// ─── 404 – User not found ──────────────────────────────────────────────────
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ResponseStructure<String>> handleUserNotFound(UserNotFoundException ex,
			HttpServletRequest request) {
		return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	// ─── 404 – Expense not found ───────────────────────────────────────────────
	@ExceptionHandler(ExpenseNotFoundException.class)
	public ResponseEntity<ResponseStructure<String>> handleExpenseNotFound(ExpenseNotFoundException ex,
			HttpServletRequest request) {
		return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	// ─── 404 – URL / endpoint not found ───────────────────────────────────────
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ResponseStructure<String>> handleNoResourceFound(NoResourceFoundException ex,
			HttpServletRequest request) {
		return buildError(HttpStatus.NOT_FOUND, "The requested endpoint does not exist: " + ex.getResourcePath(),
				request);
	}

	// ─── 409 – Duplicate email ─────────────────────────────────────────────────
	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ResponseStructure<String>> handleDuplicateEmail(DuplicateEmailException ex,
			HttpServletRequest request) {
		return buildError(HttpStatus.CONFLICT, ex.getMessage(), request);
	}

	// ─── 401 – Wrong password ──────────────────────────────────────────────────
	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<ResponseStructure<String>> handleInvalidPassword(InvalidPasswordException ex,
			HttpServletRequest request) {
		return buildError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
	}

	// ─── 403 – Custom unauthorized access ─────────────────────────────────────
	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<ResponseStructure<String>> handleUnauthorizedAccess(UnauthorizedAccessException ex,
			HttpServletRequest request) {
		return buildError(HttpStatus.FORBIDDEN, ex.getMessage(), request);
	}

	// ─── 403 – Spring Security AccessDeniedException ──────────────────────────
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseStructure<String>> handleAccessDenied(AccessDeniedException ex,
			HttpServletRequest request) {
		return buildError(HttpStatus.FORBIDDEN, "Access denied: you do not have permission to perform this action",
				request);
	}

	// ─── 400 – Invalid date range ──────────────────────────────────────────────
	@ExceptionHandler(InvalidDateRangeException.class)
	public ResponseEntity<ResponseStructure<String>> handleInvalidDateRange(InvalidDateRangeException ex,
			HttpServletRequest request) {
		return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	// ─── 400 – @Valid field-level validation ──────────────────────────────────
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseStructure<String>> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		String errorMsg = ex.getBindingResult().getFieldErrors().stream()
				.map(e -> e.getField() + ": " + e.getDefaultMessage()).findFirst().orElse("Validation failed");
		return buildError(HttpStatus.BAD_REQUEST, errorMsg, request);
	}

	// ─── 400 – @Validated constraint on path/query params ─────────────────────
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ResponseStructure<String>> handleConstraintViolation(ConstraintViolationException ex,
			HttpServletRequest request) {
		String errorMsg = ex.getConstraintViolations().stream().map(v -> v.getPropertyPath() + ": " + v.getMessage())
				.findFirst().orElse("Constraint violation");
		return buildError(HttpStatus.BAD_REQUEST, errorMsg, request);
	}

	// ─── 400 – Malformed JSON body ─────────────────────────────────────────────
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseStructure<String>> handleBadJson(HttpMessageNotReadableException ex,
			HttpServletRequest request) {
		return buildError(HttpStatus.BAD_REQUEST, "Invalid request body or JSON format", request);
	}

	// ─── 400 – Wrong path variable type ───────────────────────────────────────
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ResponseStructure<String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
			HttpServletRequest request) {
		String msg = "Invalid value for parameter '" + ex.getName() + "': expected type "
				+ ex.getRequiredType().getSimpleName();
		return buildError(HttpStatus.BAD_REQUEST, msg, request);
	}

	// ─── 400 – Required query param missing ───────────────────────────────────
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ResponseStructure<String>> handleMissingParam(MissingServletRequestParameterException ex,
			HttpServletRequest request) {
		String msg = "Required parameter '" + ex.getParameterName() + "' of type " + ex.getParameterType()
				+ " is missing";
		return buildError(HttpStatus.BAD_REQUEST, msg, request);
	}

	// ─── 500 – Catch-all ───────────────────────────────────────────────────────
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseStructure<String>> handleGeneral(Exception ex, HttpServletRequest request) {
		return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage(),
				request);
	}

	// ─── 400 – Invalid or already-used reset token

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ResponseStructure<String>> handleInvalidToken(InvalidTokenException ex,
			HttpServletRequest request) {
		return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}

	// ─── 400 – Expired reset token

	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<ResponseStructure<String>> handleTokenExpired(TokenExpiredException ex,
			HttpServletRequest request) {
		return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
	}
}