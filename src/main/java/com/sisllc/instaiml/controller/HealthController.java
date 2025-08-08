package com.sisllc.instaiml.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/health")
public class HealthController {

    @GetMapping
    public Mono<String> index() {
        return Mono.just("Hello, Reactive Spring Boot!");
    }
    
    
}
