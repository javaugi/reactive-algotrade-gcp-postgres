/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.model.Member;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class MemberGenerator extends DataGeneratorBase {

    public static Member generate(String insurancePlanId) {
        Member member = Member.builder()
            .id(UUID.randomUUID().toString())
            .insurancePlanId(insurancePlanId)
            .name(FAKER.name().fullName())
            .gender(FAKER.demographic().sex())
            .tobaccoUser(FAKER.bool().bool())
            .birthDate(FAKER.date().birthday(20, 90).toInstant().atOffset(ZoneOffset.UTC))
            .enrollmentDate(FAKER.date().past(FAKER.number().numberBetween(1, 365), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .terminationDate(FAKER.date().future(FAKER.number().numberBetween(1, 365), TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.UTC))
            .build();
        
        return member;
    }    
    
    public static Member generate(DatabaseClient dbClient, String insurancePlanId) {
        Member member = generate(insurancePlanId);
        log.trace("member {}", insert(dbClient, member).subscribe());
        return member;
    }    
    
    public static Mono<Long> insert(DatabaseClient dbClient, Member member) {
        return dbClient.sql("""
            INSERT INTO members (id, insurance_plan_id, name, gender, tobacco_user, birth_date, enrollment_dDate, termination_date) 
                    VALUES (:id, :insurancePlanId, :name, :gender, :tobaccoUser, :birthDate, :enrollmentDate, :terminationDate) 
            """)
            .bind("id", member.getId())
            .bind("insurancePlanId", member.getInsurancePlanId())
            .bind("name", member.getName())
            .bind("gender", member.getGender())
            .bind("tobaccoUser", member.isTobaccoUser())
            .bind("birthDate", member.getBirthDate())
            .bind("enrollmentDate", member.getEnrollmentDate())
            .bind("terminationDate", member.getTerminationDate())            
            .fetch()
            .rowsUpdated();
    }        
}
