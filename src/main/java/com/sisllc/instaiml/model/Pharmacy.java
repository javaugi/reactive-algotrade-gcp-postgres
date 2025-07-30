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
@Table("pharmacies")
public class Pharmacy {
    @Id
    private String id;

    @Column("pharmacy_code")
    private String pharmacyCode;
    private String name;
    
    private String email;
    private String phone;
    private String address;
    
    @CreatedDate
    @Column("created_date")
    private OffsetDateTime createdDate;   

    @LastModifiedDate
    @Column("updated_date")
    private OffsetDateTime updatedDate;      
}