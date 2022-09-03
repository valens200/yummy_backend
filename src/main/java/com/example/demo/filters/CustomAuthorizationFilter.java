package com.example.demo.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j

public class CustomAuthorizationFilter  extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getServletPath().equals("/yummy/login") ){
            filterChain.doFilter(request,response);
        }else {
            String authorizationHeader = request.getHeader("Authorizationn");
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")){
           try{
               Algorithm algorithm = Algorithm.HMAC256("valens".getBytes(StandardCharsets.UTF_8));
               String token = authorizationHeader.substring("Bearer".length());
               JWTVerifier verifier = JWT.require(algorithm).build();
               DecodedJWT decodedJWT = verifier.verify(token);
               String username = decodedJWT.getSubject();
               log.info("username {}", username);
               String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
               Collection<GrantedAuthority> authorities =  new ArrayList<>();
               Arrays.stream(roles).forEach(role->{
                   authorities.add(new SimpleGrantedAuthority(role));
               });
               UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null,authorities );
               SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
               filterChain.doFilter(request, response);

           }catch (Exception exception){
               Map<String, String> messages = new HashMap<>();
               messages.put("error_message", exception.getMessage());
               new ObjectMapper().writeValue(response.getOutputStream(), messages);
           }
            }else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
