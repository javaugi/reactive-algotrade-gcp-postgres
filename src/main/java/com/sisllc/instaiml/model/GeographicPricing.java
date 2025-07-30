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
@Table("geographicPricings")
public class GeographicPricing {
    @Id 
    private String id;
    
    @Column("insurance_plan_id")
    private String insurancePlanId;
        
    @Column("zip_code")
    private String zipCode;

    @Column("state_abbr")
    private String stateAbbr;
    
    @Column("adjustment_factor")
    private BigDecimal adjustmentFactor;
    
    @Column("rating_area")
    private int ratingArea;

    @Column("effective_date")
    private OffsetDateTime effectiveDate;

    @Column("expiration_date")
    private OffsetDateTime expirationDate;

}
