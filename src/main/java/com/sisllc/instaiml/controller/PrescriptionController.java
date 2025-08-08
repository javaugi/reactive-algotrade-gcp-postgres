/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.controller;

import com.sisllc.instaiml.model.Prescription;
import com.sisllc.instaiml.repository.PrescriptionRepository;
import com.sisllc.instaiml.service.PrescriptionService;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {

    /*
    ◦ Purpose of @Transactional: It defines the scope of a single database transaction. All operations within a @Transactional method
        either commit together or roll back together if an exception occurs. It's crucial for maintaining data consistency.
    ◦ Challenges in Reactive (R2DBC) Context:
        ▪ Thread-Bound Nature (JPA/JDBC): Traditional @Transactional relies on ThreadLocal for managing connections and transactions,
            which is inherently blocking and doesn't fit the non-blocking, asynchronous nature of reactive programming. Reactive streams
            can switch threads multiple times within an operation.
        ▪ Spring Boot's Reactive Transaction Management: Spring Boot provides specific reactive transaction management (e.g., R2dbcTransactionManager)
            which works with ReactiveTransactionManager and requires the TransactionalOperator or TransactionalOperator.execute for explicit
            transactional boundaries within reactive chains. Simply putting @Transactional on a method that returns a Mono or Flux might work in
            simple cases due to Spring's reactive transaction synchronization, but it's not always robust for complex reactive flows.
        ▪ Explicit TransactionalOperator: For more control and reliability in complex reactive scenarios, you typically inject TransactionalOperator
            and wrap your reactive chain:
     */

    private final PrescriptionService prescriptionService;
    private final PrescriptionRepository prescriptionRepository;
    private final TransactionalOperator transactionalOperator;

    @GetMapping
    public Flux<Prescription> getAllPrescriptions() {
        return this.prescriptionRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public Mono<Prescription> getPrescriptionById(@PathVariable String id) {
        return prescriptionRepository.findById(id);
    }
    
    @PostMapping("/add")
    @Transactional
    public Mono<Prescription> addPrescription(@RequestBody Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }

    @PostMapping("/create")
    @Transactional
    public Mono<Prescription> create(@RequestBody Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }

    @PostMapping("/createtrans")
    @Transactional
    public Mono<Prescription> createInTransaction(Prescription entity) {
        //status is a ReactiveTransactionStatus
        return transactionalOperator
            .execute(status -> prescriptionRepository.save(entity)
            .flatMap(savedEntity -> {
            // Potentially other reactive operations that need to be part of the same transaction
            log.info("saving in trans ...");
            return Mono.just(savedEntity);
                })
                .doOnError(e -> status.setRollbackOnly()) // You can explicitly mark for rollback
        ).next(); // Execute returns a Flux, use .next() for a single result
    }

    @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<Prescription>> updateStatus(@PathVariable String id, @RequestBody String status) {        
        return prescriptionService.updateStatus(id, status)
            .map(ResponseEntity::ok)
            .onErrorResume(RuntimeException.class, 
                e -> Mono.just(ResponseEntity.notFound().build()));
    }
    
    @PatchMapping("/{id}")
    @Transactional
    public Mono<Prescription> updatePrescription(@PathVariable String id, @RequestBody Prescription prescriptionUpdate) {
        if (prescriptionUpdate == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "userUpdate empty with ID: " + id));
        }
        // 1. Find the existing client by ID
        return prescriptionRepository.findById(id)
                .flatMap(prescription -> {
                    // 2. If client is found, apply the updates from clientUpdate
                    //    Only update fields that are provided/intended to be updated.
                    //    Here, we're assuming clientUpdate contains the full updated state.
                    //    For partial updates (PATCH), you'd check for nulls in clientUpdate.
                    
                    prescription.setStatus(prescriptionUpdate.getStatus());
                    prescription.setUpdatedDate(OffsetDateTime.now());
                    // Copy other fields as needed

                    // 3. Save the updated existingClient
                    return prescriptionRepository.save(prescription);
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Prescription not found with ID: " + id)));
        // 4. Handle the case where the client with the given ID is not found.
        // If findById returns an empty Mono, switchIfEmpty will emit an error.
    }
    
    @PatchMapping
    @Transactional
    public Mono<Prescription> updatePrescription(@RequestBody Prescription prescriptionUpdate) {
        if (prescriptionUpdate == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "userUpdate empty with ID: " + prescriptionUpdate.getId()));
        }
        // 1. Find the existing client by ID
        return prescriptionRepository.findById(prescriptionUpdate.getId())
                .flatMap(prescription -> {
                    // 2. If client is found, apply the updates from clientUpdate
                    //    Only update fields that are provided/intended to be updated.
                    //    Here, we're assuming clientUpdate contains the full updated state.
                    //    For partial updates (PATCH), you'd check for nulls in clientUpdate.
                    
                    prescription.setStatus(prescriptionUpdate.getStatus());
                    prescription.setUpdatedDate(OffsetDateTime.now());
                    // Copy other fields as needed

                    // 3. Save the updated existingClient
                    return prescriptionRepository.save(prescription);
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Prescription not found with ID: " + prescriptionUpdate.getId())));
        // 4. Handle the case where the client with the given ID is not found.
        // If findById returns an empty Mono, switchIfEmpty will emit an error.
    }
    
    
    @DeleteMapping("/{id}")
    @Transactional
    public Mono<Void> deleteById(@PathVariable String id) {
        return prescriptionRepository.deleteById(id).then();
    }
    
    
    @DeleteMapping
    @Transactional
    public Mono<Void> delete(@RequestBody Prescription prescription) {
        return prescriptionRepository.delete(prescription).then();
    }
}
