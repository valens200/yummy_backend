package com.example.demo.repository;

import com.example.demo.models.Product;
import com.example.demo.models.ProductResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository  extends JpaRepository<Product, Integer> {
    Product getProductByProductName(String productName);
//    @Query("select new com.example.demo.models.ProductResult(id, restaurant_location,email ,password,restaurant_name , category,product_cost,product_name ) from YummyReatuarant y join Product p on y.id = p.id")
//    ProductResult getProducts();
}
