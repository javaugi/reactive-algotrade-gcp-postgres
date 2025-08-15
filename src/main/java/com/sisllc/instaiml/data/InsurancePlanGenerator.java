/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.model.InsurancePlan;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class InsurancePlanGenerator extends DataGeneratorBase {

    public static InsurancePlan generate(String insuranceCompanyId) {
        InsurancePlan insurancePlan = InsurancePlan.builder()
            .id(UUID.randomUUID().toString())
            .insuranceCompanyId(insuranceCompanyId)
            .planName("PLAN-NAME-" + FAKER.number().digits(6))
            .planType(FAKER.options().option("HMO", "PPO", "EPO", "POS", "HDHP"))
            .networkType(FAKER.options().option("In-Network", "Out-of-Network"))
            .tierLevel(FAKER.options().option("Bronze", "Silver", "Gold", "Platinum", "Catastrophic"))
            .active(FAKER.bool().bool())
            .effectiveDate(FAKER.date().past(FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .expirationDate(FAKER.date().future(FAKER.number().numberBetween(100, 300), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .createdDate(FAKER.date().past(FAKER.number().numberBetween(30, 90), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .updatedDate(FAKER.date().past(FAKER.number().numberBetween(1, 30), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        return insurancePlan;
    }    
    
    public static InsurancePlan generate(DatabaseClient dbClient, String insuranceCompanyId) {
        InsurancePlan insurancePlan = generate(insuranceCompanyId);
        log.trace("insurancePlan {}", insert(dbClient, insurancePlan).subscribe());
        return insurancePlan;
    }    
    
    public static Mono<Long> insert(DatabaseClient dbClient, InsurancePlan insurancePlan) {
        return dbClient.sql("""
            INSERT INTO insurancePlans (id, insurance_company_id, plan_name, plan_type, network_type, 
                            tier_level, active, effective_date, expiration_date, created_date, updated_date) 
                    VALUES (:id, :insuranceCompanyId, :planName, :planType, :networkType, 
                            :tierLevel, :active, :effectiveDate, :expirationDate, :createdDate, :updatedDate) 
            """)
            .bind("id", insurancePlan.getId())
            .bind("insuranceCompanyId", insurancePlan.getInsuranceCompanyId())
            .bind("planName", insurancePlan.getPlanName())
            .bind("planType", insurancePlan.getPlanType())
            .bind("networkType", insurancePlan.getNetworkType())
            .bind("tierLevel", insurancePlan.getTierLevel())
            .bind("active", insurancePlan.isActive())
            .bind("effectiveDate", insurancePlan.getEffectiveDate())
            .bind("expirationDate", insurancePlan.getExpirationDate())
            .bind("createdDate", insurancePlan.getCreatedDate())
            .bind("updatedDate", insurancePlan.getUpdatedDate())            
            .fetch()
            .rowsUpdated();
    }    
}
