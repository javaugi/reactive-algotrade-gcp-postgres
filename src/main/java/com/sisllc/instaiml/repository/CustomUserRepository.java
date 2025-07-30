package com.sisllc.instaiml.repository;

import com.sisllc.instaiml.model.User;
import reactor.core.publisher.Mono;

public interface CustomUserRepository {
    Mono<User> saveUser(User user);
}

