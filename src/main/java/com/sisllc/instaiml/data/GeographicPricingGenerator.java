/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.model.GeographicPricing;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class GeographicPricingGenerator extends DataGeneratorBase {

    public static GeographicPricing generate(String insurancePlanId) {
        String state = getStateAbbr();
        String zipCode = getZipCodeByStateAbbr(state);
        
        GeographicPricing geographicPricing = GeographicPricing.builder()
            .id(UUID.randomUUID().toString())
            .insurancePlanId(insurancePlanId)
            .zipCode(zipCode)
            .stateAbbr(state)
            .adjustmentFactor(new BigDecimal(JAVA_FAKER.number().randomDouble(2, 1, 5)))
            .ratingArea(JAVA_FAKER.number().numberBetween(1, 5))
            .effectiveDate(JAVA_FAKER.date().past(JAVA_FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .expirationDate(JAVA_FAKER.date().future(JAVA_FAKER.number().numberBetween(100, 300), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        return geographicPricing;
    }   
       
    public static GeographicPricing generate(DatabaseClient dbClient, String insurancePlanId) {
        String state = getStateAbbr();
        String zipCode = getZipCodeByStateAbbr(state);
        
        GeographicPricing geographicPricing = GeographicPricing.builder()
            .id(UUID.randomUUID().toString())
            .insurancePlanId(insurancePlanId)
            .zipCode(zipCode)
            .stateAbbr(state)
            .adjustmentFactor(new BigDecimal(JAVA_FAKER.number().randomDouble(2, 1, 5)))
            .ratingArea(JAVA_FAKER.number().numberBetween(1, 5))
            .effectiveDate(JAVA_FAKER.date().past(JAVA_FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .expirationDate(JAVA_FAKER.date().future(JAVA_FAKER.number().numberBetween(100, 300), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        log.trace("geographicPricing {}", insert(dbClient, geographicPricing).subscribe());                
        return geographicPricing;
    }   
       
    public static Mono<Long> insert(DatabaseClient dbClient, GeographicPricing geographicPricing) {
        return dbClient.sql("""
            INSERT INTO geographicPricings (id, insurance_plan_id, zip_code, state_abbr, rating_area, 
                            adjustment_factor, effective_date, expiration_date) 
                    VALUES (:id, :insurancePlanId, :zipCode, :stateAbbr, :ratingArea, 
                            :adjustmentFactor, :effectiveDate, :expirationDate) 
            """)
            .bind("id", geographicPricing.getId())
            .bind("insurancePlanId", geographicPricing.getInsurancePlanId())
            .bind("zipCode", geographicPricing.getZipCode())
            .bind("stateAbbr", geographicPricing.getStateAbbr())
            .bind("ratingArea", geographicPricing.getRatingArea())            
            .bind("adjustmentFactor", geographicPricing.getAdjustmentFactor())
            .bind("effectiveDate", geographicPricing.getEffectiveDate())
            .bind("expirationDate", geographicPricing.getExpirationDate())            
            .fetch()
            .rowsUpdated();
    }            
}
