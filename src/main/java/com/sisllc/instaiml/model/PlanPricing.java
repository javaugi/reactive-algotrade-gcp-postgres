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
@Table("planPricings")
public class PlanPricing {
    @Id 
    private String id;
    
    @Column("insurance_plan_id")
    private String insurancePlanId;
        
    @Column("pricing_type")
    private String pricingType; //(e.g. premium, deductible),
    
    @Column("base_premium")
    private BigDecimal basePremium;
    
    @Column("tobacco_surcharge")
    private BigDecimal tobaccoSurcharge;
    
    @Column("family_coverage_adjustment")
    private BigDecimal familyCoverageAdjustment;
    
    @Column("misc_adjustment")
    private BigDecimal miscAdjustment;
    
    @Column("age_bracket")
    private String ageBracket;

    @Column("coverage_level")
    private String coverageLevel;
    
    @Column("effective_date")
    private OffsetDateTime effectiveDate;

    @Column("expiration_date")
    private OffsetDateTime expirationDate;

}
