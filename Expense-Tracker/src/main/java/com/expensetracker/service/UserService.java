package com.expensetracker.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.expensetracker.dao.UserDao;
import com.expensetracker.dto.ChangePasswordRequest;
import com.expensetracker.dto.UpdateProfileRequest;
import com.expensetracker.dto.ResponseStructure;
import com.expensetracker.entity.User;
import com.expensetracker.enums.Roles;
import com.expensetracker.exception.DuplicateEmailException;
import com.expensetracker.exception.InvalidPasswordException;
import com.expensetracker.exception.UnauthorizedAccessException;
import com.expensetracker.exception.UserNotFoundException;

@Service
public class UserService {

	@Autowired
	public UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${app.upload.dir:uploads/profile-photos}")
	private String uploadDir;

	// ─── Register ──────────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<User>> saveUser(User user) {
		if (userDao.findByEmail(user.getEmail()) != null) {
			throw new DuplicateEmailException("User with email '" + user.getEmail() + "' already exists");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(Roles.USER);

		ResponseStructure<User> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.CREATED.value());
		response.setMessage("User registered successfully");
		response.setData(userDao.saveUser(user));
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// ─── Login ─────────────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<User>> login(String email, String password) {
		User user = userDao.findByEmail(email);

		if (user == null) {
			throw new UserNotFoundException("No account found with email: " + email);
		}
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new InvalidPasswordException("Incorrect password. Please try again.");
		}

		ResponseStructure<User> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Login successful");
		response.setData(user);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Get Profile ───────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<User>> getProfile(Long userId, Long requesterId) {
		verifyOwnerOrAdmin(requesterId, userId);

		User user = userDao.getUserById(userId);
		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}

		ResponseStructure<User> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Profile fetched successfully");
		response.setData(user);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Update Profile ────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<User>> updateProfile(Long userId, UpdateProfileRequest request,
			Long requesterId) {
		verifyOwnerOrAdmin(requesterId, userId);

		User user = userDao.getUserById(userId);
		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}

		// Check new email is not taken by another account
		if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
			User existingWithEmail = userDao.findByEmail(request.getEmail());
			if (existingWithEmail != null && !existingWithEmail.getId().equals(userId)) {
				throw new DuplicateEmailException(
						"Email '" + request.getEmail() + "' is already in use by another account");
			}
		}

		user.setFullName(request.getFullName());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());

		ResponseStructure<User> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Profile updated successfully");
		response.setData(userDao.saveUser(user));
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Upload Profile Photo ──────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<String>> uploadProfilePhoto(Long userId, MultipartFile file,
			Long requesterId) {
		verifyOwnerOrAdmin(requesterId, userId);

		User user = userDao.getUserById(userId);
		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}

		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("Please select a file to upload");
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new IllegalArgumentException("Only image files (JPG, PNG, WEBP) are allowed");
		}

		String originalFilename = file.getOriginalFilename();
		String extension = "";
		if (originalFilename != null && originalFilename.contains(".")) {
			extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
		}

		if (!extension.matches("\\.(jpg|jpeg|png|webp)")) {
			throw new IllegalArgumentException("Allowed formats: JPG, JPEG, PNG, WEBP");
		}

		try {
			Path uploadPath = Paths.get(uploadDir);
			Files.createDirectories(uploadPath);

			// Delete old photo if exists
			if (user.getProfilePhoto() != null) {
				Files.deleteIfExists(Paths.get(user.getProfilePhoto()));
			}

			String newFilename = "user_" + userId + "_" + UUID.randomUUID() + extension;
			Path filePath = uploadPath.resolve(newFilename);
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			String relativePath = uploadDir + "/" + newFilename;
			user.setProfilePhoto(relativePath);
			userDao.saveUser(user);

			ResponseStructure<String> response = new ResponseStructure<>();
			response.setStatusCode(HttpStatus.OK.value());
			response.setMessage("Profile photo uploaded successfully");
			response.setData(relativePath);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (IOException e) {
			throw new RuntimeException("Failed to upload profile photo: " + e.getMessage());
		}
	}

	// ─── Change Password ───────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<String>> changePassword(ChangePasswordRequest request) {
		User user = userDao.getUserById((long) request.getUserId());

		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + request.getUserId());
		}
		if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
			throw new InvalidPasswordException("Old password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userDao.saveUser(user);

		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Password changed successfully");
		response.setData("Success");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Get All Users (ADMIN only) ────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<List<User>>> getAllUsers() {
		ResponseStructure<List<User>> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("All users fetched successfully");
		response.setData(userDao.getAllUsers());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Get User By ID ────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<User>> getUserById(Long id) {
		User user = userDao.getUserById(id);
		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + id);
		}

		ResponseStructure<User> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("User fetched successfully");
		response.setData(user);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Delete User (ADMIN only) ──────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<String>> deleteUser(Long id) {
		User user = userDao.getUserById(id);
		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + id);
		}

		userDao.deleteUser(id);

		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("User deleted successfully");
		response.setData("Deleted user with id: " + id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Update User Role (ADMIN only) ────────────────────────────────────────
	public ResponseEntity<ResponseStructure<User>> updateUserRole(Long id, Roles role) {
		User user = userDao.getUserById(id);
		if (user == null) {
			throw new UserNotFoundException("User not found with id: " + id);
		}

		user.setRole(role);
		userDao.saveUser(user);

		ResponseStructure<User> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("User role updated to " + role);
		response.setData(user);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Verify requester is ADMIN ─────────────────────────────────────────────
	public void verifyAdminAccess(Long requesterId) {
		User requester = userDao.getUserById(requesterId);
		if (requester == null) {
			throw new UserNotFoundException("Requesting user not found with id: " + requesterId);
		}
		if (requester.getRole() != Roles.ADMIN) {
			throw new UnauthorizedAccessException("Access denied: only ADMIN can perform this action");
		}
	}

	// ─── Verify owner or ADMIN ────────────────────────────────────────────────
	private void verifyOwnerOrAdmin(Long requesterId, Long targetUserId) {
		User requester = userDao.getUserById(requesterId);
		if (requester == null) {
			throw new UserNotFoundException("Requesting user not found with id: " + requesterId);
		}
		if (requester.getRole() != Roles.ADMIN && !requesterId.equals(targetUserId)) {
			throw new UnauthorizedAccessException("Access denied: you can only access your own profile");
		}
	}
}