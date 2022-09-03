package com.example.demo.controller;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.models.Product;
import com.example.demo.models.Role;
import com.example.demo.models.YummyRestaurant;
import com.example.demo.models.YummyUser;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.YummyRestaurantRepository;
import com.example.demo.repository.YummyUserRepository;
import com.example.demo.service.YummyRestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/yummy")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @Autowired
    YummyRestaurantService yummyRestaurantService;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public  String index(){
        return "hello, wellcome on yummy";
    }

    @GetMapping("/restaurants")
    public  List<YummyRestaurant> getAllRestaurants(){
        return yummyRestaurantService.getAllRestaurants();
    }

    @GetMapping("/restaurant/{id}")
    public Optional<YummyRestaurant> getRestaurantById(@PathVariable  int id){
        return  yummyRestaurantService.getYummyRestaurantById(id);
    }
    @GetMapping("/users")
    public List<YummyUser> getAllUsers(){
        return  yummyRestaurantService.getAllYummyUsers();
    }
    @GetMapping("/roles")
    public  List<Role> getAllRoles() {
        return yummyRestaurantService.getAllRoles();
    }
    @GetMapping("/role/{id}")
    public  Optional<Role> getRoleById(@PathVariable int id){
        return yummyRestaurantService.getRoleById(id);
    }
    @GetMapping("/products")
    public  List<Product> getAllProducts(){
        return yummyRestaurantService.getAllProducts();
    }
    @GetMapping("product/{id}")
    public  Optional<Product> getProductById(@PathVariable int id){
        return yummyRestaurantService.getProductById(id);
    }
    @GetMapping("/user/{id}")
    public Optional<YummyUser> getUserById(@PathVariable int id){
        return yummyRestaurantService.getYummyUserById(id);
    }

    @PostMapping("/saveUser")
    public YummyUser saveUser(@RequestBody  YummyUser user , HttpServletRequest request, HttpServletResponse response) throws IOException {
         user.setUserPasswod(passwordEncoder.encode(user.getUserPasswod()));
        return yummyRestaurantService.RegisterNewYummyUser(user, request, response);
    }
    @PostMapping("/saveRole")
    public  Role saveRole(@RequestBody  Role role){
        return yummyRestaurantService.RegisterNewRole(role);
    }
    @PostMapping("/saveRestaurant")
    public YummyRestaurant saveRestaurant(@RequestBody  YummyRestaurant restaurant, HttpServletRequest request, HttpServletResponse response) throws  Exception{
             restaurant.setPassword(passwordEncoder.encode(restaurant.getPassword()));
            return yummyRestaurantService.RegisterNewYummyRestaurant(restaurant, request, response);

    }
    @PostMapping("/saveProduct")
    public  Product saveProduct(@RequestBody  Product product){
        return yummyRestaurantService.RegisterNewProduct(product);
    }

    @PostMapping("/user/{name}")
    public YummyRestaurant AddRoletoUser(@PathVariable String name){
        return yummyRestaurantService.addRoleTouser(name, "USER");
    }
    @PostMapping("/admin/{name}")
    public YummyRestaurant AddRoletoUse(@PathVariable String name){
        return yummyRestaurantService.addRoleTouser(name, "ADMIN");
    }
    @PostMapping("/refresh")
    public  void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader("Authorizationn");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
            try{
                String token = authorizationHeader.substring("Bearer".length());
                Algorithm algorithm = Algorithm.HMAC256("valens".getBytes(StandardCharsets.UTF_8));
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);
                String username = decodedJWT.getSubject();
                YummyRestaurant isAvailable = yummyRestaurantService.getYummyRestaurantByRestaurantName(username);
                if(isAvailable == null){
                    log.info("user not found {} ", isAvailable.getEmail());
                }else {
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    Arrays.stream(roles).forEach(role->{
                        authorities.add(new SimpleGrantedAuthority(role));
                    });

                    String accessToken = JWT.create()
                            .withSubject(isAvailable.getEmail())
                            .withClaim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                            .withIssuer(request.getRequestURI().toString())
                            .withExpiresAt(new Date(System.currentTimeMillis() + 2 * 60 * 1000))
                            .sign(algorithm);

                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("accessToken", accessToken);
                    tokens.put("refreshToken", token);
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);

                }
            }catch (Exception exception){
                Map<String, String> messages = new HashMap<>();
                messages.put("error_message", exception.getMessage());
                new ObjectMapper().writeValue(response.getOutputStream(), messages);
            }
        }
    }

}
