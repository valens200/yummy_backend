package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "restaurantID")
    private int resaurantId;
    @Column(name = "clinetId")
    private int clientId;
    @Column(name = "orderName")
    private String orderName;
    @Column(name = "orderPrice")
    private String orderPrice;

}
