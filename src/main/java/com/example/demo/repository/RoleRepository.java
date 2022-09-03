package com.example.demo.repository;

import com.example.demo.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository  extends JpaRepository<Role, Integer> {
    Role findByroleName(String roleName);

}
