package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Product {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "productName")
    private String productName;
    @Column(name = "productCost")
    private int productCost;
    @Column( name="category")
    private String category;
}
