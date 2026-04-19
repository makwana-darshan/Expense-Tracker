package com.expensetracker.entity;

import java.time.LocalDateTime;

import com.expensetracker.enums.Roles;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Full name is required")
	@Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
	private String fullName;

	@NotBlank(message = "Email is required")
	@Email(message = "Please provide a valid email address")
	@Column(unique = true, nullable = false)
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters")
	@Column(nullable = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@Enumerated(EnumType.STRING)
	private Roles role;

	private Boolean status;

	@Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Please provide a valid phone number")
	private String phone;

	// Relative path to uploaded photo e.g. "uploads/profile-photos/user_1_uuid.jpg"
	private String profilePhoto;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public User() {
	}

	public User(String fullName, String email, String password, Roles role, Boolean status) {
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.role = role;
		this.status = status;
	}

	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.status = true;
	}

	@PreUpdate
	public void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}