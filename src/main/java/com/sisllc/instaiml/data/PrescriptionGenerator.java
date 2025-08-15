/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.model.Prescription;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class PrescriptionGenerator extends DataGeneratorBase {
    
    public static Prescription generate(String patientId, String physicianId, String medicationId, String pharmacyId) {
        Prescription prescription = Prescription.builder()
            .id(UUID.randomUUID().toString())
            .patientId(patientId)
            .physicianId(physicianId)
            .medicationId(medicationId)
            .pharmacyId(pharmacyId)
            .quantity(FAKER.number().numberBetween(1, 30))
            .refills(FAKER.number().numberBetween(1, 4))
            .status(FAKER.options().option("active", "expired", "filled", "canelled"))
            .fillDate(FAKER.date().past(FAKER.number().numberBetween(2, 20), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .prescriptionDate(FAKER.date().past(FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))            
            .createdDate(FAKER.date().past(FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .updatedDate(FAKER.date().past(FAKER.number().numberBetween(1, 30), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        return prescription;
    }    
    
    public static Prescription generate(DatabaseClient dbClient, String patientId, String physicianId, String medicationId, String pharmacyId) {
        Prescription prescription = generate(patientId, physicianId, medicationId, pharmacyId);
        log.trace("prescription {}", insert(dbClient, prescription).subscribe());
        return prescription;
    }    
    
    public static Mono<Long> insert(DatabaseClient dbClient, Prescription prescription) {
        return dbClient.sql("""
            INSERT INTO prescriptions (id, patient_id, physician_id, pharmacy_id, medication_id, quantity, 
                            refills, status, fill_date, prescription_date, created_date, updated_date) 
                    VALUES (:id, :patientId, :physicianId, :pharmacyId, :medicationId, :quantity, 
                            :refills, :status, :fillDate, :prescriptionDate, :createdDate, :updatedDate) 
            """)
            .bind("id", prescription.getId())
            .bind("patientId", prescription.getPatientId())
            .bind("physicianId", prescription.getPhysicianId())
            .bind("pharmacyId", prescription.getPharmacyId())
            .bind("medicationId", prescription.getMedicationId())
            .bind("quantity", prescription.getQuantity())
            .bind("refills", prescription.getRefills())
            .bind("status", prescription.getStatus())
            .bind("fillDate", prescription.getFillDate())
            .bind("prescriptionDate", prescription.getPrescriptionDate())
            .bind("createdDate", prescription.getCreatedDate())
            .bind("updatedDate", prescription.getUpdatedDate())
            .fetch()
            .rowsUpdated();
    }     
}
