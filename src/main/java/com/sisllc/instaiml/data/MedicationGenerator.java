/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.model.Medication;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class MedicationGenerator extends DataGeneratorBase {

    public static Medication generate() {
        Medication medication = Medication.builder()
            .id(UUID.randomUUID().toString())
            .name(JAVA_FAKER.medical().medicineName())
            .description(JAVA_FAKER.medical().medicineName())
            .dosage(JAVA_FAKER.number().numberBetween(1, 3) + " per day")
            .unit(JAVA_FAKER.number().numberBetween(20, 300) + " mg")
            .createdDate(JAVA_FAKER.date().past(JAVA_FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .updatedDate(JAVA_FAKER.date().past(JAVA_FAKER.number().numberBetween(1, 30), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        return medication;
    }
    
    public static Medication generate(DatabaseClient dbClient) {
        Medication medication = generate();
        log.trace("medication {}", insert(dbClient, medication).subscribe());
        return medication;
    }
    
    public static Mono<Long> insert(DatabaseClient dbClient, Medication medication) {
        return dbClient.sql("""
            INSERT INTO medications (id, name, description, dosage, unit, created_date, updated_date) 
                    VALUES (:id, :name, :description, :dosage, :unit, :createdDate, :updatedDate) 
            """)
            .bind("id", medication.getId())
            .bind("name", medication.getName())
            .bind("description", medication.getDescription().getBytes())
            .bind("dosage", medication.getDosage())
            .bind("unit", medication.getUnit())
            .bind("createdDate", medication.getCreatedDate())
            .bind("updatedDate", medication.getUpdatedDate())
            .fetch()
            .rowsUpdated();
    }        
}
