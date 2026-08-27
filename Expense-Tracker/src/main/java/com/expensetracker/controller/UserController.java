package com.expensetracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.expensetracker.dto.ChangePasswordRequest;
import com.expensetracker.dto.LoginResponse;
import com.expensetracker.dto.UpdateProfileRequest;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.User;
import com.expensetracker.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	// POST /user/saveUser — public
	@PostMapping("/saveUser")
	public ResponseEntity<ResponseStructure<User>> saveUser(@Valid @RequestBody User user) {
		return userService.saveUser(user);
	}

	// POST /user/login — public, returns JWT
	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<LoginResponse>> login(@RequestBody User user) {
		return userService.login(user.getEmail(), user.getPassword());
	}

	// GET /user/profile/{userId} — requires token
	@GetMapping("/profile/{userId}")
	public ResponseEntity<ResponseStructure<User>> getProfile(@PathVariable Long userId) {
		return userService.getProfile(userId);
	}

	// PUT /user/profile/{userId} — requires token
	@PutMapping("/profile/{userId}")
	public ResponseEntity<ResponseStructure<User>> updateProfile(@PathVariable Long userId,
			@Valid @RequestBody UpdateProfileRequest request) {
		return userService.updateProfile(userId, request);
	}

	// POST /user/profile/{userId}/photo — requires token
	@PostMapping(value = "/profile/{userId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResponseStructure<String>> uploadProfilePhoto(@PathVariable Long userId,
			@RequestPart("file") MultipartFile file) {
		return userService.uploadProfilePhoto(userId, file);
	}

	// PUT /user/changePassword — requires token
	@PutMapping("/changePassword")
	public ResponseEntity<ResponseStructure<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
		return userService.changePassword(request);
	}

	// PUT /user/{userId}/budget — set monthly budget
	@PutMapping("/{userId}/budget")
	public ResponseEntity<ResponseStructure<String>> updateBudget(@PathVariable Long userId,
			@RequestParam java.math.BigDecimal budget) {
		return userService.updateBudget(userId, budget);
	}
}