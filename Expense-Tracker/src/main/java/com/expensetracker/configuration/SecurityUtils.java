package com.expensetracker.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.expensetracker.dao.UserDao;
import com.expensetracker.entity.User;
import com.expensetracker.exception.UserNotFoundException;

@Component
public class SecurityUtils {

	@Autowired
	private UserDao userDao;

	public User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			throw new UserNotFoundException("No authenticated user found");
		}
		String email = auth.getName();
		User user = userDao.findByEmail(email);
		if (user == null) {
			throw new UserNotFoundException("Authenticated user not found in database");
		}
		return user;
	}

	public Long getCurrentUserId() {
		return getCurrentUser().getId();
	}
}