/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.model;

import java.math.BigDecimal;
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
@Table("coverageDetails")
public class CoverageDetail {
    @Id 
    private String id;
    
    @Column("insurance_plan_id")
    private String insurancePlanId;
    
    @Column("deductible_individual")
    private BigDecimal deductibleIndividual;

    @Column("deductible_family")
    private BigDecimal deductibleFamily;

    @Column("oop_max_individual")
    private BigDecimal oopMaxIndividual;

    @Column("oop_max_family")
    private BigDecimal oopMaxFamily;

    @Column("primary_care_copay")
    private BigDecimal primaryCareCopay;

    @Column("specialist_copay")
    private BigDecimal specialistCopay;

    @Column("er_copay")
    private BigDecimal erCopay;

    @Column("prescription_tier1")
    private BigDecimal prescriptionTier1;

    @Column("prescription_tier2")
    private BigDecimal prescriptionTier2;

    @Column("prescription_tier3")
    private BigDecimal prescriptionTier3;
    
    @Column("effective_date")
    private OffsetDateTime effectiveDate;

    @Column("expiration_date")
    private OffsetDateTime expirationDate;
}
