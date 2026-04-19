package com.expensetracker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.User;
import com.expensetracker.enums.Roles;
import com.expensetracker.service.UserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserService userService;

	// GET /admin/users?requesterId=1
	@GetMapping("/users")
	public ResponseEntity<ResponseStructure<List<User>>> getAllUsers(@RequestParam Long requesterId) {
		userService.verifyAdminAccess(requesterId);
		return userService.getAllUsers();
	}

	// GET /admin/users/{id}?requesterId=1
	@GetMapping("/users/{id}")
	public ResponseEntity<ResponseStructure<User>> getUserById(@PathVariable Long id, @RequestParam Long requesterId) {
		userService.verifyAdminAccess(requesterId);
		return userService.getUserById(id);
	}

	// DELETE /admin/users/{id}?requesterId=1
	@DeleteMapping("/users/{id}")
	public ResponseEntity<ResponseStructure<String>> deleteUser(@PathVariable Long id, @RequestParam Long requesterId) {
		userService.verifyAdminAccess(requesterId);
		return userService.deleteUser(id);
	}

	// PUT /admin/users/{id}/role?role=ADMIN&requesterId=1
	@PutMapping("/users/{id}/role")
	public ResponseEntity<ResponseStructure<User>> updateUserRole(@PathVariable Long id, @RequestParam Roles role,
			@RequestParam Long requesterId) {
		userService.verifyAdminAccess(requesterId);
		return userService.updateUserRole(id, role);
	}
}