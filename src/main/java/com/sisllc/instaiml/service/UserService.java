package com.sisllc.instaiml.service;

import com.sisllc.instaiml.dto.UserDto;
import com.sisllc.instaiml.exception.UserNotFoundException;
import com.sisllc.instaiml.model.User;
import com.sisllc.instaiml.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Specialized registration method (can include additional logic)
    public Mono<User> register(User user) {
        // Add any registration-specific logic here
        return save(user); // Reuses the save() method
    }

    // Generic save method that automatically hashes passwords
    public Mono<User> save(User user) {
        return hashUserPassword(user)
            .flatMap(userRepository::save);
    }

    public Mono<User> saveUser(User user) {
        return Mono.just(user)
            .filter(u -> u.getPassword() != null)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Password cannot be null")))
            .flatMap(u -> {
                if (u.getPassword().startsWith("$2a$")) {
                    return Mono.just(u); // Skip hashing if already hashed
                }
            String hashedPassword = passwordEncoder.encode(u.getPassword());
            u.setPassword(hashedPassword);
            return Mono.just(u);
            })
            .flatMap(userRepository::save);
    }

    private boolean isPasswordHashed(String password) {
        return password.startsWith("$2a$");
    }


    // Private helper method for password hashing
    private Mono<User> hashUserPassword(User user) {
        if (user.getPassword() == null) {
            return Mono.error(new IllegalArgumentException("Password cannot be null"));
        }

        // Skip hashing if already hashed (BCrypt pattern)
        if (user.getPassword().startsWith("$2a$")) {
            return Mono.just(user);
        }

        return Mono.just(user)
            .map(u -> {
            u.setPassword(passwordEncoder.encode(u.getPassword()));
                return u;
            });
    }

    public Mono<User> login(String username, String rawPassword) {
        return userRepository.findByUsername(username)
            .flatMap(user
                -> user.verifyPassword(rawPassword, passwordEncoder)
                .filter(valid -> valid)
                .map(__ -> user)
            )
            .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid credentials")));
    }

    public Mono<User> saveUser(UserDto userDto) {
        User newUser = new User(
                null,                 // let the record generate a UUID if null
                userDto.name(),
                userDto.username(),
                userDto.password(),
                userDto.roles(),
                userDto.email(),
                userDto.phone(),
                userDto.firstName(),
                userDto.lastName(),
                userDto.age(),
                userDto.city(),
                userDto.createdDate(),
                userDto.updatedDate()
        );
        return userRepository.save(newUser);
    }

    public Mono<User> updateUser(String userId, UserDto userDto) {
        return userRepository.findById(userId)
                .flatMap(existingUser -> {
                    User updatedUser = new User(
                            existingUser.getId(),
                            userDto.name(),
                            userDto.username(),
                            userDto.password(),
                            userDto.roles(),
                            userDto.email(),
                            userDto.phone(),
                            userDto.firstName(),
                            userDto.lastName(),
                            userDto.age(),
                            userDto.city(),
                            userDto.createdDate(),
                            userDto.updatedDate()
                    );
                    return userRepository.save(updatedUser);
                });
    }

    public Mono<Void> deleteUserById(String userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException("No user found for id: " + userId)))
                .flatMap(userRepository::delete);
    }

    public Flux<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public Mono<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Mono<List<String>> getUserIds() {
        return userRepository.getUserIds()
                .collectList();
    }
}
