package com.expensetracker.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String token;

	@Column(nullable = false)
	private LocalDateTime expiryTime;

	@Column(nullable = false)
	private Boolean used = false;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private LocalDateTime createdAt;

	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() { return id; }

	public String getToken() { return token; }
	public void setToken(String token) { this.token = token; }

	public LocalDateTime getExpiryTime() { return expiryTime; }
	public void setExpiryTime(LocalDateTime expiryTime) { this.expiryTime = expiryTime; }

	public Boolean getUsed() { return used; }
	public void setUsed(Boolean used) { this.used = used; }

	public User getUser() { return user; }
	public void setUser(User user) { this.user = user; }

	public LocalDateTime getCreatedAt() { return createdAt; }
}