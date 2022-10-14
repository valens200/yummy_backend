package com.example.demo.controller;


import ch.qos.logback.core.encoder.EchoEncoder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.models.*;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.YummyRestaurantRepository;
import com.example.demo.repository.YummyUserRepository;
import com.example.demo.service.YummyRestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

import static org.springframework.http.HttpStatus.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/yummy")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @Autowired
    YummyRestaurantService yummyRestaurantService;

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")

    public Authentication login(YummyRestaurant restaurant, HttpServletRequest request, HttpServletResponse response){
        String username = restaurant.getEmail();
        String password = restaurant.getPassword();
        try{
            response.setStatus(OK.value());
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password ));
        }catch(BadCredentialsException exception){
            response.setStatus(400);
            log.error("erorr : {}", exception.getMessage());
            return null;
        }

    }


    @GetMapping
    public  String index(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> messages = new HashMap<>();
        messages.put("message", "hello, wellcome to yummy");
        new ObjectMapper().writeValue(response.getOutputStream(),  messages);
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
//        log.info("available {}", user);?
     try{
         if(user.getUserFullname() == "" || user.getUserFullname() == null || user.getUserName() == null || user.getUserName() =="" || user.getEmail() == null ||
                 user.getEmail() == "" || user.getPassword() == null || user.getPassword() == ""){
             Map<String, String> messages = new HashMap<>();
             messages.put("message", "Invalid inputs please fill out all the fields are required");
             new ObjectMapper().writeValue(response.getOutputStream(), messages);
             response.setStatus(BAD_REQUEST.value());
             return null;
         }else{
             user.setPassword(passwordEncoder.encode(user.getPassword()));
             yummyRestaurantService.RegisterNewYummyUser(user, request, response);
             Map<String, String> messages = new HashMap<>();
             Collection<Role> roles = user.getRoles();
             Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
             roles.stream().forEach(role -> {
                 authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
             });
             Algorithm algorithm = Algorithm.HMAC256("valens".getBytes(StandardCharsets.UTF_8));
             String access_Token = JWT.create()
                     .withSubject(user.getUserName())
                     .withClaim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                     .withIssuer(request.getRequestURI().toString())
                     .withIssuedAt(new Date(System.currentTimeMillis()))
                     .withExpiresAt(new Date(System.currentTimeMillis() + 2 * 10 * 6000))
                     .sign(algorithm);
             String refresh_Token = JWT.create()
                     .withSubject(user.getUserName())
                     .withClaim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                     .withIssuer(request.getRequestURI())
                     .withIssuedAt(new Date(System.currentTimeMillis()))
                     .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 10 * 6000))
                     .sign(algorithm);
             messages.put("message", "user with  username  " + user.getUserName() + " registered successfully");
             messages.put("access_Token", access_Token);
             messages.put("refresh_TOken", refresh_Token);
             response.setStatus(OK.value());
             new ObjectMapper().writeValue(response.getOutputStream(), messages);
             return yummyRestaurantService.getYummyUserByUsername(user.getUserName());



         }
     }catch(Exception exception){
         log.error("Error {}", exception.getMessage());
         return null;
     }
    }
    @PostMapping("/saveRole")
    public  Role saveRole(@RequestBody  Role role){

        return yummyRestaurantService.RegisterNewRole(role);
    }
    @PostMapping("/saveOrder")
    public  Orders saveOrder(@RequestBody Orders order , HttpServletRequest request, HttpServletResponse response){
        return yummyRestaurantService.saveOrder(order);
    }
    @PostMapping("/saveRestaurant")
    public YummyRestaurant saveRestaurant(@RequestBody  YummyRestaurant restaurant, HttpServletRequest request, HttpServletResponse response) throws  Exception {
        try {
            if (restaurant.getRestaurantName() == null || restaurant.getRestaurantName() == "" || restaurant.getRestaurantLocation() == null  || restaurant.getRestaurantLocation() == "" || restaurant.getEmail() == null || restaurant.getEmail() == "" || restaurant.getPassword() == null || restaurant.getPassword() == "") {
                Map<String, String> error_messages = new HashMap<>();
                error_messages.put("message", "Invalid inputs please fillout all fields are required");
                response.setStatus(BAD_REQUEST.value());
                new ObjectMapper().writeValue(response.getOutputStream(), error_messages);
                return null;
            } else {
                yummyRestaurantService.RegisterNewYummyRestaurant(restaurant, request, response);
                Map<String, String> messages = new HashMap<>();
                messages.put("message", "Restaurant  " + restaurant.getRestaurantName() + " registered successfully");

                Collection<Role> roles = new ArrayList<>();
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                roles.stream().forEach(role -> {
                    authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
                });
                Algorithm algorithm = Algorithm.HMAC256("valens".getBytes(StandardCharsets.UTF_8));
                String access_Token = JWT.create()
                        .withSubject(restaurant.getRestaurantName())
                        .withClaim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                        .withIssuer(request.getRequestURI())
                        .withIssuedAt(new Date(System.currentTimeMillis()))
                        .withExpiresAt(new Date(System.currentTimeMillis() + 2 * 10 * 6000))
                        .sign(algorithm);
                String refresh_Token = JWT.create()
                        .withSubject(restaurant.getRestaurantName())
                        .withClaim("roles", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                        .withIssuer(request.getRequestURI())
                        .withIssuedAt(new Date(System.currentTimeMillis()))
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 10 * 6000))
                        .sign(algorithm);
                log.info("name {}", restaurant.getRestaurantName());
                messages.put("refresh_Token", refresh_Token);
                messages.put("access_Token", access_Token);
                restaurant.setPassword(passwordEncoder.encode(restaurant.getPassword()));
                response.setStatus(OK.value());
                new ObjectMapper().writeValue(response.getOutputStream(), messages);
                return null;
            }
        } catch (Exception exception) {
            log.info("eroorr; {}", exception.getMessage());
            return null;
        }
    }


    @PostMapping("/saveProduct")
    public  Product saveProduct(@RequestBody  Product product, HttpServletResponse response, HttpServletRequest request){
         try{
             if( product.getProductName() == null ||  product.getCategory() == null){
                 Map<String, String> error_messages = new HashMap<>();
                 error_messages.put("message", "Invalid inputs please fillout all fields are required");
                 response.setStatus(BAD_REQUEST.value());
                 new ObjectMapper().writeValue(response.getOutputStream(), error_messages);
                 return null;
             }else{
                 Map<String, String> error_messages = new HashMap<>();
                 error_messages.put("message", "Product with " + product.getProductName() + " was created successfully");
                 response.setStatus(OK.value());
                 new ObjectMapper().writeValue(response.getOutputStream(), error_messages);
                 return yummyRestaurantService.RegisterNewProduct(product);
             }

         }catch (Exception exception){
            log.error("error {}" , exception.getMessage());
             return null;
         }

    }

    @PostMapping("/user/{name}")
    public YummyRestaurant AddRoletoUser(@PathVariable String name){
        return yummyRestaurantService.addRoleTouser(name, "USER");
    }
    @PostMapping("/admin/{name}")
    public YummyRestaurant AddRoletoUse(@PathVariable String name){
        return yummyRestaurantService.addRoleTouser(name, "ADMIN");
    }

    @PostMapping("/addOrderTouser")
    public  Optional<YummyRestaurant> addOrderTouser(@RequestBody Orders order, HttpServletRequest request, HttpServletResponse response){
        try{
            if(order.getOrderName() == null){
                log.error("invalid inputs");
                return null;
            }else{
                return yummyRestaurantService.addOrderToRestaurant(order.getResaurantId(), order.getOrderName());
            }

        }catch (Exception exception){
            log.error("error {}", exception.getMessage());
            return null;
        }
    }

    @PostMapping("/addProductTOuser")
    public   Optional<YummyRestaurant> addProductTouser(@RequestBody Product product , HttpServletResponse response , HttpServletRequest request) throws  Exception
    {
            if(product.getProductName() == null || product.getCategory() == null){
                log.error("error all inputs are required");
                return null;
            }else{
                return yummyRestaurantService.addProductToRestaurant(product.getProductName());
            }
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
