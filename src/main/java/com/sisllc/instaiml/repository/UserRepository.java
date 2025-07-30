package com.sisllc.instaiml.repository;

import com.sisllc.instaiml.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, String> {
    // Return a Flux of string IDs
    @Query("SELECT id FROM users")
    Flux<String> getUserIds();

    @Query("SELECT * FROM users")
    Flux<User> getAllUsers();

    Mono<User> findByUsername(String username);
}
