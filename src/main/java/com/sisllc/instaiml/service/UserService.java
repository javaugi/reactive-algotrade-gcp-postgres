package com.sisllc.instaiml.service;

import com.sisllc.instaiml.dto.UserDto;
import com.sisllc.instaiml.exception.UserNotFoundException;
import com.sisllc.instaiml.model.User;
import com.sisllc.instaiml.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> save(User user) {
        return userRepository.save(user);
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
