/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.model.PlanPricing;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class PlanPricingGenerator extends DataGeneratorBase {
   
    public static PlanPricing generate(String insurancePlanId) {
        PlanPricing planPricing = PlanPricing.builder()
            .id(UUID.randomUUID().toString())
            .insurancePlanId(insurancePlanId)
            .pricingType(FAKER.options().option("premium", "deductible", "copay", "coinsurance", "adjustment"))
            .basePremium(new BigDecimal(FAKER.number().numberBetween(1000, 9000)).setScale(2, RoundingMode.UP))
            .tobaccoSurcharge(new BigDecimal(FAKER.number().numberBetween(10, 30)).setScale(2, RoundingMode.UP))
            .familyCoverageAdjustment(new BigDecimal(FAKER.number().numberBetween(10, 30)).setScale(2, RoundingMode.UP).negate())
            .miscAdjustment(new BigDecimal(FAKER.number().numberBetween(0, 20)).setScale(2, RoundingMode.UP).negate())
            .ageBracket(generateAgeGroupBracket())
            .coverageLevel(FAKER.options().option("Bronze", "Silver", "Gold", "Platinum", "Catastrophic"))
            .effectiveDate(FAKER.date().past(FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .expirationDate(FAKER.date().future(FAKER.number().numberBetween(100, 300), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        return planPricing;
    }    

    public static PlanPricing generate(DatabaseClient dbClient, String insurancePlanId) {
        PlanPricing planPricing = generate(insurancePlanId);
        log.trace("planPricing {}", insert(dbClient, planPricing).subscribe());
        return planPricing;
    }    
        
    public static Mono<Long> insert(DatabaseClient dbClient, PlanPricing planPricing) {
        return dbClient.sql("""
            INSERT INTO planPricings (id, insurance_plan_id, pricing_type, base_premium, tobacco_surcharge, 
                            family_coverage_adjustment, misc_adjustment, age_bracket, coverage_level, effective_date, expiration_date) 
                    VALUES (:id, :insurancePlanId, :pricingType, :basePremium, :tobaccoSurcharge, 
                            :familyCoverageAdjustment, :miscAdjustment, :ageBracket, :coverageLevel, :effectiveDate, :expirationDate) 
            """)
            .bind("id", planPricing.getId())
            .bind("insurancePlanId", planPricing.getInsurancePlanId())
            .bind("pricingType", planPricing.getPricingType())
            .bind("basePremium", planPricing.getBasePremium())
            .bind("tobaccoSurcharge", planPricing.getTobaccoSurcharge())
            .bind("familyCoverageAdjustment", planPricing.getFamilyCoverageAdjustment())            
            .bind("miscAdjustment", planPricing.getMiscAdjustment())
            .bind("ageBracket", planPricing.getAgeBracket())
            .bind("coverageLevel", planPricing.getCoverageLevel())
            .bind("effectiveDate", planPricing.getEffectiveDate())
            .bind("expirationDate", planPricing.getExpirationDate())            
            .fetch()
            .rowsUpdated();
    }     
}
