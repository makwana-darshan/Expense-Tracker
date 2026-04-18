package com.expensetracker.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.expensetracker.entity.User;
import com.expensetracker.repository.UserRepository;

@Repository
public class UserDao {

	@Autowired
	private UserRepository userRepository;

	// ✅ Create User
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	// ✅ Get All Users
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	// ✅ Get User by ID
	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
	}

	// ✅ Get User by Email
	public User findByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}

	// ✅ Delete User
	public void deleteUser(Long id) {
		userRepository.deleteById(id);
	}
}
