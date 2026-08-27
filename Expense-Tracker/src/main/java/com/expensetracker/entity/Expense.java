package com.expensetracker.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.expensetracker.enums.Categories;
import com.expensetracker.enums.PaymentMethod;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "expenses")
public class Expense {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Title is required")
	@Size(max = 100, message = "Title must not exceed 100 characters")
	private String title;

	@Size(max = 255, message = "Description must not exceed 255 characters")
	private String description;

	@NotNull(message = "Amount is required")
	@DecimalMin(value = "0.01", message = "Amount must be greater than 0")
	@Column(nullable = false)
	private BigDecimal amount;

	@NotNull(message = "Date is required")
	private LocalDate date;

	private LocalTime time;

	@NotNull(message = "Category is required")
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Categories category;

	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	// 🔹 Constructors
	public Expense() {
	}

	public Expense(String title, String description, BigDecimal amount, LocalDate date, LocalTime time,
			Categories category, PaymentMethod paymentMethod, User user) {
		this.title = title;
		this.description = description;
		this.amount = amount;
		this.date = date;
		this.time = time;
		this.category = category;
		this.paymentMethod = paymentMethod;
		this.user = user;
	}

	// 🔹 Auto set timestamps
	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	public void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	// 🔹 Getters and Setters

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public Categories getCategory() {
		return category;
	}

	public void setCategory(Categories category) {
		this.category = category;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}