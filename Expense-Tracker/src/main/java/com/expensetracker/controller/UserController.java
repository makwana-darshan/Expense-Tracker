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

	// POST /user/saveUser
	@PostMapping("/saveUser")
	public ResponseEntity<ResponseStructure<User>> saveUser(@Valid @RequestBody User user) {
		return userService.saveUser(user);
	}

	// POST /user/login
	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<User>> login(@RequestBody User user) {
		return userService.login(user.getEmail(), user.getPassword());
	}

	// GET /user/profile/{userId}?requesterId=1
	@GetMapping("/profile/{userId}")
	public ResponseEntity<ResponseStructure<User>> getProfile(@PathVariable Long userId,
			@RequestParam Long requesterId) {
		return userService.getProfile(userId, requesterId);
	}

	// PUT /user/profile/{userId}?requesterId=1
	@PutMapping("/profile/{userId}")
	public ResponseEntity<ResponseStructure<User>> updateProfile(@PathVariable Long userId,
			@Valid @RequestBody UpdateProfileRequest request, @RequestParam Long requesterId) {
		return userService.updateProfile(userId, request, requesterId);
	}

	// POST /user/profile/{userId}/photo?requesterId=1
	// Content-Type: multipart/form-data | field name: "file"
	@PostMapping(value = "/profile/{userId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResponseStructure<String>> uploadProfilePhoto(@PathVariable Long userId,
			@RequestPart("file") MultipartFile file, @RequestParam Long requesterId) {
		return userService.uploadProfilePhoto(userId, file, requesterId);
	}

	// PUT /user/changePassword
	@PutMapping("/changePassword")
	public ResponseEntity<ResponseStructure<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
		return userService.changePassword(request);
	}
}