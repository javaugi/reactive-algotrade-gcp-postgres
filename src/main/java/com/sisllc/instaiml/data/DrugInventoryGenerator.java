/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.model.DrugInventory;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class DrugInventoryGenerator extends DataGeneratorBase {

    public static DrugInventory generate(String pharmacyId, String medicationId) {
        DrugInventory drugInventory =  DrugInventory.builder()
            .id(UUID.randomUUID().toString())
            .pharmacyId(pharmacyId)
            .medicationId(medicationId)
            .quantity(JAVA_FAKER.number().numberBetween(15, 100))
            .reorderThreshold(JAVA_FAKER.number().numberBetween(10, 20))
            .createdDate(JAVA_FAKER.date().past(JAVA_FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .updatedDate(JAVA_FAKER.date().past(JAVA_FAKER.number().numberBetween(1, 30), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        return drugInventory;
    }    
       
    public static DrugInventory generate(DatabaseClient dbClient, String pharmacyId, String medicationId) {
        DrugInventory drugInventory =  DrugInventory.builder()
            .id(UUID.randomUUID().toString())
            .pharmacyId(pharmacyId)
            .medicationId(medicationId)
            .quantity(JAVA_FAKER.number().numberBetween(15, 100))
            .reorderThreshold(JAVA_FAKER.number().numberBetween(10, 20))
            .createdDate(JAVA_FAKER.date().past(JAVA_FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .updatedDate(JAVA_FAKER.date().past(JAVA_FAKER.number().numberBetween(1, 30), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        log.trace("drugInventory {}", insert(dbClient, drugInventory).subscribe());        
        return drugInventory;
    }    
       
    public static Mono<Long> insert(DatabaseClient dbClient, DrugInventory drugInventory) {
        return dbClient.sql("""
            INSERT INTO drugInventories (id, pharmacy_id, medication_id, quantity, reorder_threshold, created_date, updated_date) 
                    VALUES (:id, :pharmacyId, :medicationId, :quantity, :reorderThreshold, :createdDate, :updatedDate) 
            """)
            .bind("id", drugInventory.getId())
            .bind("pharmacyId", drugInventory.getPharmacyId())
            .bind("medicationId", drugInventory.getMedicationId())
            .bind("quantity", drugInventory.getQuantity())
            .bind("reorderThreshold", drugInventory.getReorderThreshold())
            .bind("createdDate", drugInventory.getCreatedDate())
            .bind("updatedDate", drugInventory.getUpdatedDate())
            .fetch()
            .rowsUpdated();
    }    
}
