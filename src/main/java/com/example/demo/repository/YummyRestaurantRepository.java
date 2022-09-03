package com.example.demo.repository;

import com.example.demo.models.YummyRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YummyRestaurantRepository extends JpaRepository<com.example.demo.models.YummyRestaurant, Integer> {
    YummyRestaurant findByEmail(String email);
}
