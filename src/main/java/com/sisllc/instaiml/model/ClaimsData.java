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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@Table("claimsData")
public class ClaimsData {
    @Id 
    private String id;
    
    @Column("insurance_plan_id")
    private String insurancePlanId;
    
    @Column("member_id")
    private String memberId;    
    
    @Column("provider_id")
    private String providerId;    
    
    @Column("billed_amount")
    private BigDecimal billedAmount;
    
    @Column("allowed_amount")
    private BigDecimal allowedAmount;
    
    @Column("paid_amount")
    private BigDecimal paidAmount;
    
    @Column("diagnosis_codes")
    private String diagnosisCodes;
    
    @Column("procedure_codes")
    private String procedureCodes;
    
    @Column("claim_status")
    private String claimStatus;
    
    @Column("service_date")
    private OffsetDateTime serviceDate;
    
    @Column("claim_date")
    private OffsetDateTime claimDate;
    
    @CreatedDate
    @Column("created_date")
    private OffsetDateTime createdDate;   

    @LastModifiedDate
    @Column("updated_date")
    private OffsetDateTime updatedDate;
}
