package com.example.demo.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    AuthenticationManager authenticationManager;

    public  CustomAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
      try{
          String email = request.getParameter("email");
          String password = request.getParameter("password");
          log.info("email {}", email);
          log.info("password {}", password);

          UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(email, password);
          return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
      }catch (Exception exception){
          response.setStatus(BAD_REQUEST.value());
          Map<String, String> messages = new HashMap<>();
          messages.put("err_message", exception.getMessage());
          try {
              new ObjectMapper().writeValue(response.getOutputStream(), messages);
          } catch (IOException e) {
              throw new RuntimeException(e);
          }
          return  null;
      }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();
        Algorithm algorithm = Algorithm.HMAC256("valens".getBytes(StandardCharsets.UTF_8));
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withIssuer(request.getRequestURI().toString())
                .withIssuedAt(new Date(System.currentTimeMillis() + 2 * 60 * 1000))
                .withClaim( "roles" , user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        String refreshToken = JWT.create()
                .withIssuer(request.getRequestURI().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                .withSubject(user.getUsername())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(BAD_REQUEST.value());
        Map<String, String> messages = new HashMap<>();
        messages.put("err_message", "Invalid email or password");
        new ObjectMapper().writeValue(response.getOutputStream(), messages);
    }

}
