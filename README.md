# Expense Tracker Application

A full-featured Expense Tracker web application built with Spring Boot.  
It helps users manage daily expenses, track spending patterns, handle recurring expenses, and view reports with secure authentication and role-based access.

## Features

- User registration and login with JWT authentication
- Role-based access control for USER and ADMIN
- Add, update, delete, and view expenses
- Category-wise filtering and date-range search
- Pagination for expense listing
- Recurring expense processing using scheduled tasks
- Dashboard with total expense and spending trends
- Category-wise and monthly/yearly expense reports
- Profile management
- Change password feature
- Forgot password and reset password flow using email
- Admin user management
- Validation and global exception handling

## Tech Stack

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- JWT
- MySQL
- REST API
- HTML
- CSS
- Bootstrap
- Maven
- Postman

## Project Structure

```bash
src
└── main
    ├── java
    │   └── com.yourpackage.expensetracker
    │       ├── controller
    │       ├── service
    │       ├── repository
    │       ├── entity
    │       ├── dto
    │       ├── exception
    │       ├── config
    │       └── scheduler
    └── resources
        ├── application.properties
        └── static
