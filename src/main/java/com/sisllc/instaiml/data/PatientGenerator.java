/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.model.Patient;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class PatientGenerator extends DataGeneratorBase {

    public static Patient generate() {
        Patient patient = Patient.builder()
            .id(UUID.randomUUID().toString())
            .name(FAKER.name().fullName())
            .birthDate(FAKER.date().birthday().toInstant().atOffset(ZoneOffset.UTC))
            .email(FAKER.internet().emailAddress())
            .phone(FAKER.phoneNumber().phoneNumber())
            .address(FAKER.address().fullAddress())
            .createdDate(FAKER.date().past(FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .updatedDate(FAKER.date().past(FAKER.number().numberBetween(1, 30), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        return patient;
    }    
    
    public static Patient generate(DatabaseClient dbClient) {
        Patient patient = generate();
        log.trace("patient {}", insert(dbClient, patient).subscribe());
        return patient;
    }    
    
    public static Mono<Long> insert(DatabaseClient dbClient, Patient patient) {
        return dbClient.sql("""
            INSERT INTO patients (id, name, birth_date, email, phone, address, created_date, updated_date) 
                    VALUES (:id, :name, :birthDate, :email, :phone, :address, :createdDate, :updatedDate) 
            """)
            .bind("id", patient.getId())
            .bind("name", patient.getName())
            .bind("birthDate", patient.getBirthDate())
            .bind("email", patient.getEmail())
            .bind("phone", patient.getPhone())
            .bind("address", patient.getAddress())
            .bind("createdDate", patient.getCreatedDate())
            .bind("updatedDate", patient.getUpdatedDate())
            .fetch()
            .rowsUpdated();
    }     
}
