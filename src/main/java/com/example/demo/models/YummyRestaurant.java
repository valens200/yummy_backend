package com.example.demo.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.Order;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class YummyRestaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  int id;

    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "restaurantName")
    private  String restaurantName;
    @Column(name = "RestaurantLocation")
    private String RestaurantLocation;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();
    @ManyToMany(targetEntity = Product.class, cascade = CascadeType.ALL)
    private  Collection<Product> availableProducts = new ArrayList<>();
    @OneToMany(targetEntity = YummyUser.class, cascade = CascadeType.ALL)
    private Collection<YummyUser> clients = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY)
    Collection<Orders> orders = new ArrayList<>();

 }
