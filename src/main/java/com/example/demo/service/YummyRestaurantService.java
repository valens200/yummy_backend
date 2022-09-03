package com.example.demo.service;

import com.example.demo.models.Product;
import com.example.demo.models.Role;
import com.example.demo.models.YummyRestaurant;
import com.example.demo.models.YummyUser;
import com.example.demo.repository.YummyRestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface YummyRestaurantService {

    List<YummyRestaurant> getAllRestaurants();
    List<Role> getAllRoles();
    List<YummyUser> getAllYummyUsers();
    List<Product> getAllProducts();

    Optional<Product> getProductById(int id);
    Optional<YummyRestaurant> getYummyRestaurantById(int id);
    Optional<YummyUser> getYummyUserById(int id);
    Optional<Role> getRoleById(int id);

    Product getProductByProductName(String productName);
    YummyRestaurant getYummyRestaurantByRestaurantName(String restaurantName);
    YummyUser getYummyUserByUsername( String userName);
    Role getRoleByName(String roleName);


    Product RegisterNewProduct(Product product);
    YummyRestaurant RegisterNewYummyRestaurant(YummyRestaurant yummyRestaurant, HttpServletRequest request,  HttpServletResponse response) throws IOException;
    YummyUser RegisterNewYummyUser(YummyUser yummyUser, HttpServletRequest request, HttpServletResponse response) throws IOException;
    Role RegisterNewRole(Role role);

    YummyRestaurant addRoleTouser(String userName, String roleName);


}
