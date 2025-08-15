/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.model.CoverageDetail;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class CoverageDetailGenerator extends DataGeneratorBase {

    public static CoverageDetail generate(String insurancePlanId) {
        CoverageDetail coverageDetail = CoverageDetail.builder()
            .id(UUID.randomUUID().toString())
            .insurancePlanId(insurancePlanId)
            .deductibleIndividual(new BigDecimal(FAKER.number().randomDouble(2, 500, 1200)))
            .deductibleFamily(new BigDecimal(FAKER.number().randomDouble(2, 1500, 2500)))
            .oopMaxIndividual(new BigDecimal(FAKER.number().randomDouble(2, 2000, 2500)))
            .oopMaxFamily(new BigDecimal(FAKER.number().randomDouble(2, 4000, 6000)))
            .primaryCareCopay(new BigDecimal(FAKER.number().randomDouble(2, 10, 30)))
            .specialistCopay(new BigDecimal(FAKER.number().randomDouble(2, 80, 200)))
            .erCopay(new BigDecimal(FAKER.number().randomDouble(2, 200, 500)))
            .prescriptionTier1(new BigDecimal(FAKER.number().randomDouble(2, 10, 20)))
            .prescriptionTier2(new BigDecimal(FAKER.number().randomDouble(2, 20, 30)))
            .prescriptionTier3(new BigDecimal(FAKER.number().randomDouble(2, 30, 50)))
            .effectiveDate(FAKER.date().past(FAKER.number().numberBetween(30, 360), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .expirationDate(FAKER.date().past(FAKER.number().numberBetween(30, 360), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        return coverageDetail;
    }    

    public static CoverageDetail generate(DatabaseClient dbClient, String insurancePlanId) {
        CoverageDetail coverageDetail = CoverageDetail.builder()
            .id(UUID.randomUUID().toString())
            .insurancePlanId(insurancePlanId)
            .deductibleIndividual(new BigDecimal(FAKER.number().randomDouble(2, 500, 1200)))
            .deductibleFamily(new BigDecimal(FAKER.number().randomDouble(2, 1500, 2500)))
            .oopMaxIndividual(new BigDecimal(FAKER.number().randomDouble(2, 2000, 2500)))
            .oopMaxFamily(new BigDecimal(FAKER.number().randomDouble(2, 4000, 6000)))
            .primaryCareCopay(new BigDecimal(FAKER.number().randomDouble(2, 10, 30)))
            .specialistCopay(new BigDecimal(FAKER.number().randomDouble(2, 80, 200)))
            .erCopay(new BigDecimal(FAKER.number().randomDouble(2, 200, 500)))
            .prescriptionTier1(new BigDecimal(FAKER.number().randomDouble(2, 10, 20)))
            .prescriptionTier2(new BigDecimal(FAKER.number().randomDouble(2, 20, 30)))
            .prescriptionTier3(new BigDecimal(FAKER.number().randomDouble(2, 30, 50)))
            .effectiveDate(FAKER.date().past(FAKER.number().numberBetween(30, 360), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .expirationDate(FAKER.date().past(FAKER.number().numberBetween(30, 360), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        log.trace("coverageDetail {}", insert(dbClient, coverageDetail).subscribe());
        return coverageDetail;
    }    
    
    public static Mono<Long> insert(DatabaseClient dbClient, CoverageDetail coverageDetail) {
        return dbClient.sql("""
            INSERT INTO coverageDetails (id, insurance_plan_id, deductible_individual, deductible_family, oop_max_individual, 
                            oop_max_family, primary_care_copay, specialist_copay, er_copay, prescription_tier1,
                            prescription_tier2, prescription_tier3, effective_date, expiration_date) 
                    VALUES (:id, :insurancePlanId, :deductibleIndividual, :deductibleFamily, :oopMaxIndividual, 
                            :oopMaxFamily, :primaryCareCopay, :specialistCopay, :erCopay, :prescriptionTier1,
                            :prescriptionTier2, :prescriptionTier3, :effectiveDate, :expirationDate) 
            """)
            .bind("id", coverageDetail.getId())
            .bind("insurancePlanId", coverageDetail.getInsurancePlanId())
            .bind("deductibleIndividual", coverageDetail.getDeductibleIndividual())
            .bind("deductibleFamily", coverageDetail.getDeductibleFamily())
            .bind("oopMaxIndividual", coverageDetail.getOopMaxIndividual())
            .bind("oopMaxFamily", coverageDetail.getOopMaxFamily())            
            .bind("primaryCareCopay", coverageDetail.getPrimaryCareCopay())
            .bind("specialistCopay", coverageDetail.getSpecialistCopay())
            .bind("erCopay", coverageDetail.getErCopay())            
            .bind("prescriptionTier1", coverageDetail.getPrescriptionTier1())
            .bind("prescriptionTier2", coverageDetail.getPrescriptionTier2())
            .bind("prescriptionTier3", coverageDetail.getPrescriptionTier3())
            .bind("effectiveDate", coverageDetail.getEffectiveDate())
            .bind("expirationDate", coverageDetail.getExpirationDate())            
            .fetch()
            .rowsUpdated();
    }        
}
