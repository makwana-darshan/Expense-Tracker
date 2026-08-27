package com.expensetracker.dto;

import com.expensetracker.entity.User;

public class LoginResponse {

	private String token;
	private String tokenType = "Bearer";
	private User user;

	public LoginResponse() {
	}

	public LoginResponse(String token, User user) {
		this.token = token;
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}