/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class InsurancePricingDto {
    String id;
    String planType;
    String planName;
    String tierLevel;
    BigDecimal avgAdjustedPremium;
    BigDecimal minPremium;
    BigDecimal maxPremium;
    Integer planCount;
    
    BigDecimal deductibleIndividual;
    BigDecimal popMaxIndividual;
    BigDecimal basePremium;
    BigDecimal estimatedAnnualCost;
    BigDecimal avgClaimCost;
    Integer claimCount;
    
    String companyName;
    String ageBracket;
    BigDecimal nonTobaccoPremium;
    BigDecimal tobaccoPremium;
    BigDecimal surchargePercentage;
    Integer tobaccoUsers;
    Integer nonTobaccoUsers;
    
    BigDecimal avgBasePremium;
    BigDecimal marketShare;
    BigDecimal avgPremium;
    BigDecimal premiumIncrease;
    BigDecimal percentIncrease;
    Integer enrolledMembers;
    BigDecimal annualPremiumVolume;    
    BigDecimal annualClaimsCosts;
    
    BigDecimal underwritingResult;
    BigDecimal lossRatio;
    
    Integer inNetworkProviders;
    Integer inNetworkClaims;
    Integer outOfNetworkClaims;
    
    BigDecimal avgInNetworkCost;
    BigDecimal avgOutOfNetworkCost;
}
