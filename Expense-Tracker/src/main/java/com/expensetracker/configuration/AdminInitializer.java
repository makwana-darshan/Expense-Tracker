package com.expensetracker.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.expensetracker.dao.UserDao;
import com.expensetracker.entity.User;
import com.expensetracker.enums.Roles;

@Component
public class AdminInitializer implements ApplicationRunner {

	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${app.admin.email:admin@expensetracker.com}")
	private String adminEmail;

	@Value("${app.admin.password:Admin@1234}")
	private String adminPassword;

	@Value("${app.admin.name:Admin}")
	private String adminName;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// Only create admin if it doesn't already exist
		if (userDao.findByEmail(adminEmail) == null) {
			User admin = new User();
			admin.setFullName(adminName);
			admin.setEmail(adminEmail);
			admin.setPassword(passwordEncoder.encode(adminPassword));
			admin.setRole(Roles.ADMIN);
			admin.setStatus(true);
			userDao.saveUser(admin);
			System.out.println("✅ Admin account created: "  );
		} else {
			System.out.println("ℹ️  Admin account already exists: " );
		}
	}
}