/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@Table("insuranceCompanies")
public class InsuranceCompany {
    @Id 
    private String id;
    
    @Column("company_code")
    private String companyCode;
    
    @Column("company_name")
    private String companyName;

    @Column("state_licenses")
    private String stateLicenses;

    @Column("financial_rating")
    private String financialRating;

    @Column("contact_info")
    private String contactInfo;
    
    @Column("market_share")
    private BigDecimal marketShare;

}
