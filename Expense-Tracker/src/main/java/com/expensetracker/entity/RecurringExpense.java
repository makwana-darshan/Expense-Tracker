package com.expensetracker.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.expensetracker.enums.Categories;
import com.expensetracker.enums.PaymentMethod;
import com.expensetracker.enums.RecurringFrequency;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "recurring_expenses")
public class RecurringExpense {

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

	@NotNull(message = "Category is required")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Categories category;

	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@NotNull(message = "Frequency is required")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RecurringFrequency frequency;

	@NotNull(message = "Start date is required")
	private LocalDate startDate;

	// null = recurs indefinitely
	private LocalDate endDate;

	@Column(nullable = false)
	private LocalDate nextDueDate;

	private LocalTime time;

	@Column(nullable = false)
	private Boolean active = true;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now();
		if (this.nextDueDate == null) {
			this.nextDueDate = this.startDate;
		}
	}

	@PreUpdate
	public void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public Long getId() { return id; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }

	public BigDecimal getAmount() { return amount; }
	public void setAmount(BigDecimal amount) { this.amount = amount; }

	public Categories getCategory() { return category; }
	public void setCategory(Categories category) { this.category = category; }

	public PaymentMethod getPaymentMethod() { return paymentMethod; }
	public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

	public RecurringFrequency getFrequency() { return frequency; }
	public void setFrequency(RecurringFrequency frequency) { this.frequency = frequency; }

	public LocalDate getStartDate() { return startDate; }
	public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

	public LocalDate getEndDate() { return endDate; }
	public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

	public LocalDate getNextDueDate() { return nextDueDate; }
	public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

	public LocalTime getTime() { return time; }
	public void setTime(LocalTime time) { this.time = time; }

	public Boolean getActive() { return active; }
	public void setActive(Boolean active) { this.active = active; }

	public User getUser() { return user; }
	public void setUser(User user) { this.user = user; }

	public LocalDateTime getCreatedAt() { return createdAt; }
	public LocalDateTime getUpdatedAt() { return updatedAt; }
}