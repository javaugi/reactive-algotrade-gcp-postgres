/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

/**
 *
 * @author javau
 */
public interface JwtUtils {
    Mono<Boolean> validateToken(String token);
    Mono<Boolean> validateToken(String token, UserDetails userDetails);
    Mono<Authentication> getAuthentication(String token);    
    Mono<String> generateToken(UserDetails userDetails);
    Mono<String> generateToken(Authentication auth);
    String extractUsername(String token);
}
