package com.expensetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.expensetracker.dao.UserDao;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.User;

@Service
public class UserService {

	@Autowired
	public UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public ResponseEntity<ResponseStructure<User>> saveUser(User user) {
		ResponseStructure<User> response = new ResponseStructure<>();
		if (userDao.findByEmail(user.getEmail()) != null) {
			response.setStatusCode(HttpStatus.CONFLICT.value());
			response.setMessage("user already exists");
			response.setData(null);
			return new ResponseEntity<ResponseStructure<User>>(response, HttpStatus.CONFLICT);
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("User created successfully");
		response.setData(userDao.saveUser(user));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
