/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import static com.sisllc.instaiml.data.DataGeneratorBase.JAVA_FAKER;
import com.sisllc.instaiml.model.User;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

@Slf4j
public class UserGenerator extends DataGeneratorBase {
    
    public static User generate(String username, PasswordEncoder passwordEncoder) {
        String firstName = JAVA_FAKER.name().firstName();
        String lastName = JAVA_FAKER.name().lastName();
        User user = User.builder()
            .id(UUID.randomUUID().toString())
            .name(firstName + " " + lastName)
            .username(username)
            .password(passwordEncoder.encode(username))
            .roles("ROLE_USER,ROLE_ADMIN")
            .email(JAVA_FAKER.internet().emailAddress())
            .phone(JAVA_FAKER.phoneNumber().phoneNumber())
            .firstName(firstName)
            .lastName(lastName)
            .age(JAVA_FAKER.number().numberBetween(18, 70))
            .city(JAVA_FAKER.address().city())
            .createdDate(JAVA_FAKER.date().past(JAVA_FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .updatedDate(JAVA_FAKER.date().past(JAVA_FAKER.number().numberBetween(1, 30), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))            
            .build();
        
        return user;
    }
    
   public static User generate(DatabaseClient dbClient, String username, PasswordEncoder passwordEncoder) {
        String firstName = JAVA_FAKER.name().firstName();
        String lastName = JAVA_FAKER.name().lastName();
        User user = User.builder()
            .id(UUID.randomUUID().toString())
            .name(firstName + " " + lastName)
            .username(username)
            .password(passwordEncoder.encode(username))
            .roles("ROLE_USER,ROLE_ADMIN")
            .email(JAVA_FAKER.internet().emailAddress())
            .phone(JAVA_FAKER.phoneNumber().phoneNumber())
            .firstName(firstName)
            .lastName(lastName)
            .age(JAVA_FAKER.number().numberBetween(18, 70))
            .city(JAVA_FAKER.address().city())
            .createdDate(JAVA_FAKER.date().past(JAVA_FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .updatedDate(JAVA_FAKER.date().past(JAVA_FAKER.number().numberBetween(1, 30), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))            
            .build();
        
        log.trace("user {}", insert(dbClient, user).subscribe());     
        return user;
    }
   
    public static Mono<Long> insert(DatabaseClient dbClient, User user) {
        return dbClient.sql("""
            INSERT INTO users (id, name, username, password, roles, email, 
                            phone, first_name, last_name, city, age, created_Date, updated_Date
                        ) VALUES (
                            :id, :name, :username, :password, :roles, :email, 
                            :phone, :firstName, :lastName, :city, :age, :createdDate, :updatedDate
                        )
            """)
            .bind("id", user.getId())
            .bind("name", user.getName())
            .bind("username", user.getUsername())
            .bind("password", user.getPassword())
            .bind("roles", user.getRoles())
            .bind("email", user.getEmail())
            .bind("phone", user.getPhone())
            .bind("firstName", user.getFirstName())
            .bind("lastName", user.getLastName())
            .bind("city", user.getCity())
            .bind("age", user.getAge())
            .bind("createdDate", user.getCreatedDate())
            .bind("updatedDate", user.getUpdatedDate())
            .fetch()
            .rowsUpdated();
    }    
    
}
