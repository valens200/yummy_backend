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
public class YummyUser {

    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "email")
    private String email;
    @Column(name = "userFullName")
    private String userFullname;
    @Column(name = "userName")
    private String userName;
    @Column(name = "userPasswod")
    private String userPasswod;

}
