package com.sisllc.instaiml.controller;

import com.sisllc.instaiml.repository.UserRepository;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/health")
public class HealthController {
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping
    public Mono<String> index() {
        long userCount = 0;
        try{
            userCount = userRepository.count().block();
        }catch(Exception ex) {
            
        }
        return Mono.just("App Running - Spring Boot " + SpringBootVersion.getVersion() + " at the server and now it is " + new Date() + ". User count =" + userCount);
    }
    
    
}
