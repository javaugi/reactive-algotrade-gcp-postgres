/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.repository;

import com.sisllc.instaiml.model.ClaimsData;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimsDataRepository extends ReactiveCrudRepository<ClaimsData, String> {
    
}
