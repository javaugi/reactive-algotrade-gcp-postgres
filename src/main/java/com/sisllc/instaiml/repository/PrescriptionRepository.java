/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.repository;

import com.sisllc.instaiml.model.Prescription;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PrescriptionRepository extends ReactiveCrudRepository<Prescription, String> {
    // Find all prescriptions by patientId (partition key)
    Flux<Prescription> findByPatientId(String patientId);

    // Find active prescriptions for a medication
    Flux<Prescription> findByMedicationIdAndStatus(String medicationId, String status);

    // Find prescriptions by physician with status filter
    Flux<Prescription> findByPhysicianIdAndStatus(String physicianId, String status);    
}
