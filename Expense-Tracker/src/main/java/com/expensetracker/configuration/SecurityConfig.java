package com.expensetracker.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private JwtFilter jwtFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						// ── Public endpoints ─────────────────────────────────────────
						.requestMatchers("/user/saveUser", "/user/login", "/user/forgot-password",
								"/user/reset-password")
						.permitAll()

						// ── Static frontend files ────────────────────────────────────
						.requestMatchers("/", "/*.html", "/*.js", "/*.css", "/*.ico", "/uploads/**", "/login",
								"/register", "/dashboard", "/add-expense", "/view-expense", "/reports",
								"/change-password")
						.permitAll()

						// ── ADMIN-only endpoints ─────────────────────────────────────
						.requestMatchers("/admin/**").hasRole("ADMIN")

						// ── Everything else requires a valid JWT ─────────────────────
						.anyRequest().authenticated())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}