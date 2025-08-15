/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import static com.sisllc.instaiml.data.DataGeneratorBase.FAKER;
import com.sisllc.instaiml.model.ClaimsData;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class ClaimsDataGenerator extends DataGeneratorBase {

    public static ClaimsData generate(String memberId, String providerId, String insurancePlanId) {
        ClaimsData claimsData = ClaimsData.builder()
            .id(UUID.randomUUID().toString())
            .memberId(memberId)
            .providerId(providerId)
            .insurancePlanId(insurancePlanId)
            .billedAmount(new BigDecimal(FAKER.number().randomDouble(2, 10, 3000)))
            .allowedAmount(new BigDecimal(FAKER.number().randomDouble(2, 10, 3000)))
            .paidAmount(new BigDecimal(FAKER.number().randomDouble(2, 10, 3000)))
            .diagnosisCodes(FAKER.medicalProcedure().icd10())
            .procedureCodes(FAKER.medicalProcedure().icd10())
            .claimStatus(FAKER.options().option("SUBMITTED", "RE-SUBMITTED", "PROCESSED", "IN-PROGRESS", "PENDING", "REJECTED", "PAID"))
            .serviceDate(FAKER.date().past(FAKER.number().numberBetween(20, 50), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .claimDate(FAKER.date().past(FAKER.number().numberBetween(5, 20), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .createdDate(FAKER.date().past(FAKER.number().numberBetween(100, 300), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .updatedDate(FAKER.date().past(FAKER.number().numberBetween(30, 100), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        return claimsData;
    }    
       
    public static ClaimsData generate(DatabaseClient dbClient, String memberId, String providerId, String insurancePlanId) {
        ClaimsData claimsData = generate(memberId, providerId, insurancePlanId);

        log.trace("claimsData {}", insert(dbClient, claimsData).subscribe());
        return claimsData;
    }    
       
    public static Mono<Long> insert(DatabaseClient dbClient, ClaimsData claimsData) {
        return dbClient.sql("""
            INSERT INTO claimsData (id, member_id, provider_id, insurance_plan_id, billedAmount, 
                            allowed_amount, paid_amount, diagnosis_codes, procedure_codes, claim_status,
                            service_date, claim_date, created_date, updated_date) 
                    VALUES (:id, :memberId, :providerId, :insurancePlanId, :billedAmount, 
                            :allowedAmount, :paidAmount, :diagnosisCodes, :procedureCodes, :claimStatus
                            :serviceDate, :claimDate, :createdDate, :updatedDate) 
            """)
            .bind("id", claimsData.getId())
            .bind("memberId", claimsData.getMemberId())
            .bind("providerId", claimsData.getProviderId())
            .bind("insurancePlanId", claimsData.getInsurancePlanId())
            .bind("billedAmount", claimsData.getBilledAmount())
            .bind("allowedAmount", claimsData.getAllowedAmount())
            .bind("paidAmount", claimsData.getPaidAmount())
            .bind("diagnosisCodes", claimsData.getDiagnosisCodes())            
            .bind("procedureCodes", claimsData.getProcedureCodes())
            .bind("claimStatus", claimsData.getClaimStatus())
            .bind("serviceDate", claimsData.getServiceDate())
            .bind("claimDate", claimsData.getClaimDate())     
            .bind("createdDate", claimsData.getCreatedDate())
            .bind("updatedDate", claimsData.getUpdatedDate())            
            .fetch()
            .rowsUpdated();
    }          
}
