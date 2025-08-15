/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.model.Physician;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class PhysicianGenerator extends DataGeneratorBase {

    public static Physician generate() {
        Physician physician = Physician.builder()
            .id(UUID.randomUUID().toString())
            .name(FAKER.name().fullName())
            .specialty(MED_SPECIALTIES.get(rand.nextInt(MED_SPECIALTIES.size())))
            .email(FAKER.internet().emailAddress())
            .phone(FAKER.phoneNumber().phoneNumber())
            .address(FAKER.address().fullAddress())
            .createdDate(FAKER.date().past(FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .updatedDate(FAKER.date().past(FAKER.number().numberBetween(1, 30), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        return physician;
    }    
    
    public static Physician generate(DatabaseClient dbClient) {
        Physician physician = generate();
        log.trace("physician {}", insert(dbClient, physician).subscribe());
        return physician;
    }    
    
    public static Mono<Long> insert(DatabaseClient dbClient, Physician physician) {
        return dbClient.sql("""
            INSERT INTO physicians (id, name, specialty, email, phone, address, created_date, updated_date) 
                    VALUES (:id, :name, :specialty, :email, :phone, :address, :createdDate, :updatedDate) 
            """)
            .bind("id", physician.getId())
            .bind("name", physician.getName())
            .bind("specialty", physician.getSpecialty())
            .bind("email", physician.getEmail())
            .bind("phone", physician.getPhone())
            .bind("address", physician.getAddress())
            .bind("createdDate", physician.getCreatedDate())
            .bind("updatedDate", physician.getUpdatedDate())
            .fetch()
            .rowsUpdated();
    }         
}
