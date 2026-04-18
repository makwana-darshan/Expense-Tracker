package com.expensetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.expensetracker.dao.UserDao;
import com.expensetracker.dto.ChangePasswordRequest;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.User;
import com.expensetracker.exception.DuplicateEmailException;
import com.expensetracker.exception.UserNotFoundException;

@Service
public class UserService {

	@Autowired
	public UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public ResponseEntity<ResponseStructure<User>> saveUser(User user) {
		ResponseStructure<User> response = new ResponseStructure<>();
		if (userDao.findByEmail(user.getEmail()) != null) {
			throw new DuplicateEmailException("User with email " + user.getEmail() + " already exists");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("User created successfully");
		response.setData(userDao.saveUser(user));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	public ResponseEntity<ResponseStructure<User>> login(String email, String password) {
		ResponseStructure<User> response = new ResponseStructure<>();

		User user = userDao.findByEmail(email);

		if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
			response.setMessage("Invalid email or password");
			response.setData(null);
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}

		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Login successful");
		response.setData(user);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public ResponseEntity<ResponseStructure<String>> changePassword(ChangePasswordRequest request) {
		ResponseStructure<String> response = new ResponseStructure<>();

		User user = userDao.getUserById((long) request.getUserId());

		if (user == null) {
		    throw new UserNotFoundException("User not found with id: " + request.getUserId());
		}

		// Check old password matches
		if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Old password is incorrect");
			response.setData(null);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		// Set new password
		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userDao.saveUser(user);

		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Password changed successfully");
		response.setData("Success");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
