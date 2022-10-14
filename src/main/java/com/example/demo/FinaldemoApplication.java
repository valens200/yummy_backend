package com.example.demo;

import com.example.demo.repository.YummyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class FinaldemoApplication {
	@Autowired
	YummyUserRepository yummyUserRepository;
	public static void main(String[] args) {

		SpringApplication.run(FinaldemoApplication.class, args);
	}
	@Bean
	BCryptPasswordEncoder PasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebMvcConfigurer webMvcConfigurer(){
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:3000")
						.allowedHeaders("*")
						.allowedMethods("POST", "PUT", "DELETE", "PUT")
						.allowCredentials(true);

			}
		};

	}

}
