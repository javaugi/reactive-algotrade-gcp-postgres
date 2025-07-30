/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@Table("drugInventories")
public class DrugInventory {
    @Id 
    private String id;
        
    @Column("pharmacy_id")
    private String pharmacyId;

    @Column("medication_id")
    private String medicationId;
    
    private Integer quantity;

    @Column("reorder_threshold")
    private Integer reorderThreshold;
    
    @CreatedDate
    @Column("created_date")
    private OffsetDateTime createdDate;   

    @LastModifiedDate
    @Column("updated_date")
    private OffsetDateTime updatedDate;
}
