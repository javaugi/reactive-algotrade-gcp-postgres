package com.sisllc.instaiml.controller;

import com.sisllc.instaiml.dto.UserDto;
import com.sisllc.instaiml.exception.UserNotFoundException;
import com.sisllc.instaiml.model.User;
import com.sisllc.instaiml.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController //Default for @RestController: JSON in/out
@RequestMapping(path = "/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public Mono<ResponseEntity<User>> addUser(@RequestBody User user) {
        log.info("addUser {}", user);
        return userService.save(user)
            .map(savedUser -> ResponseEntity.status(HttpStatus.CREATED).body(savedUser))
            // Optionally handle errors:
            .onErrorResume(e -> {
                log.error("Error addUser: " + e.getMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
            });
    }

    // Create new user
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<User>> createUser(@RequestBody UserDto userDto) {
        log.info("createUser {}", userDto);
        return userService.saveUser(userDto)
            .map(savedUser -> ResponseEntity.status(HttpStatus.CREATED).body(savedUser))
            // Optionally handle errors:
            .onErrorResume(e -> {
                log.error("Error createUser: " + e.getMessage());
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null));
            });
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<User> getAllUsers() {
        log.info("getAllUsers ");
        return userService.getAllUsers()
            .limitRate(20);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<User>> getUserById(@PathVariable("id") String id) {
        log.info("getUserById id {}", id);
        return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build())
            .onErrorResume(Exception.class, ex -> {
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            });
    }

    @GetMapping(value = "/user-ids", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<String>>> getUserIds(@PathVariable("id") String ids) {
        log.info("getUserIds id {}", ids);
        return userService.getUserIds()
            .map(ResponseEntity::ok)
            .onErrorResume(Exception.class, ex -> {
                return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList()));
            });
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<User>> updateUser(@PathVariable String id, @Valid @RequestBody UserDto userDto,
        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        log.info("Received PUT request for user ID: {} with data: {}, Authorization header: {}", id, userDto, authHeader);

        return userService.updateUser(id, userDto)
            .map(updatedUser -> {
                log.info("Successfully updateUser: {}", updatedUser);
                return ResponseEntity.ok(updatedUser);
            })
            .switchIfEmpty(Mono.just("dummy")
                .doOnNext(dummy -> log.info("Error updateUser dummy {}", dummy))
                .then(Mono.empty())
            )
            .doOnError(e -> log.error("Error updating user: {}", e.getMessage()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteUserById(@PathVariable("id") String id) {
        log.info("deleteUserById id {}", id);
        return userService.deleteUserById(id)
            .then(Mono.just(ResponseEntity.noContent().build()))
            .onErrorResume(UserNotFoundException.class, ex
                -> Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()))
            )
            );
    }

}
