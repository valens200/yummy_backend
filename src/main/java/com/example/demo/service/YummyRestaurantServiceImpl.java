package com.example.demo.service;

import com.example.demo.models.Product;
import com.example.demo.models.Role;
import com.example.demo.models.YummyRestaurant;
import com.example.demo.models.YummyUser;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.YummyRestaurantRepository;
import com.example.demo.repository.YummyUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.undo.StateEdit;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@Slf4j
public class YummyRestaurantServiceImpl implements  YummyRestaurantService, UserDetailsService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    YummyRestaurantRepository yummyRestaurantRepository;
    @Autowired
    YummyUserRepository yummyUserRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        YummyRestaurant restaurant = yummyRestaurantRepository.findByEmail(email);
        if(restaurant == null){
            log.info("User not found ");
        }else{
            log.info("User found with username {} ", email);
        }
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Collection<Role> roles = restaurant.getRoles();
        roles.stream().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        });
        return new User(restaurant.getEmail(), restaurant.getPassword(), authorities);
    }

    @Override
    public List<YummyRestaurant> getAllRestaurants() {
        return yummyRestaurantRepository.findAll();
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public List<YummyUser> getAllYummyUsers() {
        return yummyUserRepository.findAll();
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(int id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<YummyRestaurant> getYummyRestaurantById(int id) {
        return yummyRestaurantRepository.findById(id);
    }

    @Override
    public  Optional<YummyUser> getYummyUserById(int id) {
        return  yummyUserRepository.findById(id);
    }

    @Override
    public Optional<Role> getRoleById(int id) {
        return roleRepository.findById(id);
    }

    @Override
    public Product getProductByProductName(String productName) {
        return productRepository.getProductByProductName(productName);
    }

    @Override
    public YummyRestaurant getYummyRestaurantByRestaurantName(String restaurantEmail) {
        return yummyRestaurantRepository.findByEmail(restaurantEmail);
    }

    @Override
    public YummyUser getYummyUserByUsername(String username) {
        return yummyUserRepository.findByEmail(username);
    }

    @Override
    public Role getRoleByName(String roleName) {
        return roleRepository.findByroleName(roleName);
    }

    @Override
    public Product RegisterNewProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public YummyRestaurant RegisterNewYummyRestaurant(YummyRestaurant yummyRestaurant, HttpServletRequest request, HttpServletResponse response) throws IOException {
        YummyRestaurant availableRestaurant = yummyRestaurantRepository.findByEmail(yummyRestaurant.getEmail());
        if(availableRestaurant != null){
            response.setStatus( BAD_REQUEST.value());
            Map<String, String> messages = new HashMap<>();
            messages.put("err_message", "Restaurant with that Email exist please login");
            new ObjectMapper().writeValue(response.getOutputStream(), messages);
            return null;
        }else{
            return yummyRestaurantRepository.save(yummyRestaurant);
        }
    }

    @Override
    public YummyUser RegisterNewYummyUser(YummyUser yummyUser, HttpServletRequest request, HttpServletResponse response) throws IOException {
        YummyUser user = yummyUserRepository.findByEmail(yummyUser.getEmail());
        if(user != null){
            response.setStatus(BAD_REQUEST.value());
            Map<String, String> messages = new HashMap<>();
            messages.put("error_message", "user with that email exist please login");
            new ObjectMapper().writeValue(response.getOutputStream(), messages);
            return  null;
        }else{
            return yummyUserRepository.save(yummyUser);
        }

    }

    @Override
    public Role RegisterNewRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public YummyRestaurant addRoleTouser(String email, String roleName) {
        YummyRestaurant user = yummyRestaurantRepository.findByEmail(email);
        Role role = roleRepository.findByroleName(roleName);
         user.getRoles().add(role);
         return yummyRestaurantRepository.save(user);

    }
}
