package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResult {
    private int id;
    private String restaurant_location;
    private String  email;
    private String password;
    private  String restaurant_name;
    private String category;
    private Integer product_cost;
    private String product_name;
}
