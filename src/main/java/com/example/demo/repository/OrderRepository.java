package com.example.demo.repository;

import com.example.demo.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Integer> {

    Orders getOrderByOrderName(String orderName);
}
