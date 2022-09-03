package com.example.demo;

import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.YummyRestaurantRepository;
import com.example.demo.repository.YummyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

}
