package com.expensetracker.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// Map clean URLs to HTML files
		registry.addViewController("/expense-tracker").setViewName("forward:/index.html");
		registry.addViewController("/login").setViewName("forward:/login.html");
		registry.addViewController("/register").setViewName("forward:/register.html");
		registry.addViewController("/dashboard").setViewName("forward:/dashboard.html");
		registry.addViewController("/add-expense").setViewName("forward:/add-expense.html");
		registry.addViewController("/view-expense").setViewName("forward:/view-expense.html");
		registry.addViewController("/reports").setViewName("forward:/reports.html");
		registry.addViewController("/change-password").setViewName("forward:/change-password.html");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}
}