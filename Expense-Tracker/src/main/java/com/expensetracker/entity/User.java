package com.expensetracker.entity;

import java.time.LocalDateTime;

import com.expensetracker.enums.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String fullName;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@Enumerated(EnumType.STRING)
	private Roles role;

	private Boolean status;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	// 🔹 Default Constructor (REQUIRED for JPA)
	public User() {
	}

	// 🔹 Parameterized Constructor
	public User(String fullName, String email, String password, Roles role, Boolean status) {
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.role = role;
		this.status = status;
	}

	// 🔹 Auto timestamp
	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.status = true; // default active
	}

	@PreUpdate
	public void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	// 🔹 Getters and Setters

	public Long getId() {
		return id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}