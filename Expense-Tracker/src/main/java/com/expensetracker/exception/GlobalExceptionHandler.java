package com.expensetracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.expensetracker.dto.ResponseStructure;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// ✅ Handle custom UserNotFoundException
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ResponseStructure<String>> handleUserNotFound(UserNotFoundException ex) {
		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.NOT_FOUND.value());
		response.setMessage(ex.getMessage());
		response.setData(null);
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	// ✅ Handle custom ExpenseNotFoundException
	@ExceptionHandler(ExpenseNotFoundException.class)
	public ResponseEntity<ResponseStructure<String>> handleExpenseNotFound(ExpenseNotFoundException ex) {
		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.NOT_FOUND.value());
		response.setMessage(ex.getMessage());
		response.setData(null);
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	// ✅ Handle custom DuplicateEmailException
	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ResponseStructure<String>> handleDuplicateEmail(DuplicateEmailException ex) {
		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.CONFLICT.value());
		response.setMessage(ex.getMessage());
		response.setData(null);
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}

	// ✅ Handle @Valid validation errors
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseStructure<String>> handleValidation(MethodArgumentNotValidException ex) {
		String errorMsg = ex.getBindingResult().getFieldErrors().stream()
				.map(e -> e.getField() + ": " + e.getDefaultMessage()).findFirst().orElse("Validation failed");
		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		response.setMessage(errorMsg);
		response.setData(null);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// ✅ Handle wrong JSON format
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ResponseStructure<String>> handleBadJson(HttpMessageNotReadableException ex) {
		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		response.setMessage("Invalid request body or JSON format");
		response.setData(null);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// ✅ Handle wrong path variable type (e.g. passing "abc" for a Long id)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ResponseStructure<String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		response.setMessage(
				"Invalid parameter: '" + ex.getName() + "' should be a valid " + ex.getRequiredType().getSimpleName());
		response.setData(null);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	// ✅ Handle all other unexpected exceptions
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseStructure<String>> handleGeneral(Exception ex) {
		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setMessage("Something went wrong: " + ex.getMessage());
		response.setData(null);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}