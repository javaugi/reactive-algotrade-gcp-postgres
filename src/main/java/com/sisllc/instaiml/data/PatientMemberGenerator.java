/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.model.PatientMember;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class PatientMemberGenerator extends DataGeneratorBase {

    public static PatientMember generate(String patientId, String memberId) {
        PatientMember patientMember = PatientMember.builder()
            .id(UUID.randomUUID().toString())
            .patientId(patientId)
            .memberId(memberId)
            .build();
        
        return patientMember;
    }    

    public static PatientMember generate(DatabaseClient dbClient, String patientId, String memberId) {
        PatientMember patientMember = generate(patientId, memberId);
        log.trace("patientMember {}", insert(dbClient, patientMember).subscribe());
        return patientMember;
    }    
    
    public static Mono<Long> insert(DatabaseClient dbClient, PatientMember patientMember) {
        return dbClient.sql("""
            INSERT INTO patientMembers (id, patient_Id, member_Id) 
                    VALUES (:id, :patientId, :memberId) 
            """)
            .bind("id", patientMember.getId())
            .bind("patientId", patientMember.getPatientId())
            .bind("memberId", patientMember.getMemberId())
            .fetch()
            .rowsUpdated();
    }
}
