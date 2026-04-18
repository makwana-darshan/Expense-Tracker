package com.expensetracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.dto.ChangePasswordRequest;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.User;
import com.expensetracker.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/saveUser")
	public ResponseEntity<ResponseStructure<User>> saveUser(@Valid @RequestBody User user) {
		return userService.saveUser(user);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseStructure<User>> login(@RequestBody User user) {
		return userService.login(user.getEmail(), user.getPassword());
	}

	@PutMapping("/changePassword")
	public ResponseEntity<ResponseStructure<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
		return userService.changePassword(request);
	}
}
