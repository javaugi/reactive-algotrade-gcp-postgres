/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.model;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@Table("members")
public class Member {
    @Id 
    private String id;
    
    @Column("insurance_plan_id")
    private String insurancePlanId;
    
    private String name;
    private String gender;
    private String address;
    
    @Column("tobacco_user")
    private boolean tobaccoUser;
    
    @Column("birth_date")
    private OffsetDateTime birthDate;

    @Column("enrollment_date")
    private OffsetDateTime enrollmentDate;

    @Column("termination_date")
    private OffsetDateTime terminationDate;

}
