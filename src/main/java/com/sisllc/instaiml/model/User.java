package com.sisllc.instaiml.model;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@Table("users")
public class User {

    @Id
    private String id;

    private String name;
    private String username;

    @Column("password")
    private String password;
    private String roles;

    private String email;
    private String phone;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    private int age;

    private String city;

    @CreatedDate
    @Column("created_date")
    private OffsetDateTime createdDate;   

    @LastModifiedDate
    @Column("updated_date")
    private OffsetDateTime updatedDate;

    // ===== REACTIVE PASSWORD HANDLING =====
    public static Mono<User> withHashedPassword(User user, PasswordEncoder encoder) {
        return Mono.just(user)
            .map(u -> {
                u.setPassword(encoder.encode(u.getPassword()));
                return u;
            });
    }

    // ===== REACTIVE PASSWORD HANDLING =====
    public static Mono<User> withHashedPassword(User user, String hashedPassword) {
        return Mono.just(user)
            .map(u -> {
                u.setPassword(hashedPassword);
                return u;
            });
    }

    // Password verification
    public Mono<Boolean> verifyPassword(String rawPassword, PasswordEncoder encoder) {
        return Mono.just(encoder.matches(rawPassword, this.password));
    }
}
