/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.security;

import com.sisllc.instaiml.exception.UserNotFoundException;
import com.sisllc.instaiml.model.User;
import com.sisllc.instaiml.repository.UserRepository;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .switchIfEmpty(Mono.error(new UserNotFoundException("User not found")))
            //.filter(muser -> passwordEncoder.matches(password, muser.getPassword()))
            .onErrorResume(e -> {
                // Handle the error and return a default value or another Mono
                return Mono.error(new UserNotFoundException("User not found"));
            })
            .map(u -> {
                // findByUsername successful, return the muser
                return u;
            }).block();
        
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(Arrays.asList(user.getRoles().split(",")).stream()
                .map(role -> new SimpleGrantedAuthority(role.trim()))
                .collect(Collectors.toList()))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
    }
    
}
