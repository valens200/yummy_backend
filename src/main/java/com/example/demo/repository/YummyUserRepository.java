package com.example.demo.repository;

import com.example.demo.models.YummyUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface YummyUserRepository extends JpaRepository<YummyUser, Integer> {
    YummyUser findByEmail(String email);
}
