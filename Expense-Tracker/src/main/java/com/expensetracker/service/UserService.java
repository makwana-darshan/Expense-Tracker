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

import com.expensetracker.configuration.JwtUtil;
import com.expensetracker.configuration.SecurityUtils;
import com.expensetracker.dao.UserDao;
import com.expensetracker.dto.ChangePasswordRequest;
import com.expensetracker.dto.LoginResponse;
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

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private SecurityUtils securityUtils;

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

	// ─── Login — returns JWT token ─────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<LoginResponse>> login(String email, String password) {
		User user = userDao.findByEmail(email);

		if (user == null)
			throw new UserNotFoundException("No account found with email: " + email);
		if (!passwordEncoder.matches(password, user.getPassword()))
			throw new InvalidPasswordException("Incorrect password. Please try again.");

		String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole().name());
		LoginResponse loginResponse = new LoginResponse(token, user);

		ResponseStructure<LoginResponse> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Login successful");
		response.setData(loginResponse);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Get Profile ───────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<User>> getProfile(Long userId) {
		User caller = securityUtils.getCurrentUser();
		verifyOwnerOrAdmin(caller, userId);

		User user = userDao.getUserById(userId);
		if (user == null)
			throw new UserNotFoundException("User not found with id: " + userId);

		ResponseStructure<User> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Profile fetched successfully");
		response.setData(user);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Update Profile ────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<User>> updateProfile(Long userId, UpdateProfileRequest request) {
		User caller = securityUtils.getCurrentUser();
		verifyOwnerOrAdmin(caller, userId);

		User user = userDao.getUserById(userId);
		if (user == null)
			throw new UserNotFoundException("User not found with id: " + userId);

		if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {
			User existing = userDao.findByEmail(request.getEmail());
			if (existing != null && !existing.getId().equals(userId))
				throw new DuplicateEmailException(
						"Email '" + request.getEmail() + "' is already in use by another account");
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
	public ResponseEntity<ResponseStructure<String>> uploadProfilePhoto(Long userId, MultipartFile file) {
		User caller = securityUtils.getCurrentUser();
		verifyOwnerOrAdmin(caller, userId);

		User user = userDao.getUserById(userId);
		if (user == null)
			throw new UserNotFoundException("User not found with id: " + userId);

		if (file == null || file.isEmpty())
			throw new IllegalArgumentException("Please select a file to upload");

		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/"))
			throw new IllegalArgumentException("Only image files (JPG, PNG, WEBP) are allowed");

		String originalFilename = file.getOriginalFilename();
		String extension = "";
		if (originalFilename != null && originalFilename.contains("."))
			extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

		if (!extension.matches("\\.(jpg|jpeg|png|webp)"))
			throw new IllegalArgumentException("Allowed formats: JPG, JPEG, PNG, WEBP");

		try {
			Path uploadPath = Paths.get(uploadDir);
			Files.createDirectories(uploadPath);

			if (user.getProfilePhoto() != null)
				Files.deleteIfExists(Paths.get(user.getProfilePhoto()));

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
		User caller = securityUtils.getCurrentUser();

		if (!passwordEncoder.matches(request.getOldPassword(), caller.getPassword()))
			throw new InvalidPasswordException("Old password is incorrect");

		caller.setPassword(passwordEncoder.encode(request.getNewPassword()));
		userDao.saveUser(caller);

		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("Password changed successfully");
		response.setData("Success");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Get All Users ─────────────────────────────────────────────────────────
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
		if (user == null)
			throw new UserNotFoundException("User not found with id: " + id);

		ResponseStructure<User> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("User fetched successfully");
		response.setData(user);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Delete User ───────────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<String>> deleteUser(Long id) {
		User user = userDao.getUserById(id);
		if (user == null)
			throw new UserNotFoundException("User not found with id: " + id);

		userDao.deleteUser(id);

		ResponseStructure<String> response = new ResponseStructure<>();
		response.setStatusCode(HttpStatus.OK.value());
		response.setMessage("User deleted successfully");
		response.setData("Deleted user with id: " + id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// ─── Update User Role ──────────────────────────────────────────────────────
	public ResponseEntity<ResponseStructure<User>> updateUserRole(Long id, Roles role) {
		User user = userDao.getUserById(id);
		if (user == null)
			throw new UserNotFoundException("User not found with id: " + id);

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
		if (requester == null)
			throw new UserNotFoundException("Requesting user not found with id: " + requesterId);
		if (requester.getRole() != Roles.ADMIN)
			throw new UnauthorizedAccessException("Access denied: only ADMIN can perform this action");
	}

	// ─── Internal helpers ──────────────────────────────────────────────────────
	private void verifyOwnerOrAdmin(User caller, Long targetUserId) {
		if (caller.getRole() != Roles.ADMIN && !caller.getId().equals(targetUserId)) {
			throw new UnauthorizedAccessException("Access denied: you can only access your own profile");
		}
	}
}