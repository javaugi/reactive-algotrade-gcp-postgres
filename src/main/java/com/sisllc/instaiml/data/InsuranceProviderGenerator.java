/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.model.InsuranceProvider;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class InsuranceProviderGenerator extends DataGeneratorBase {

    public static InsuranceProvider generate() {
        InsuranceProvider insuranceProvider = InsuranceProvider.builder()
            .id(UUID.randomUUID().toString())
            .providerName(FAKER.company().name())
            .specialty(FAKER.company().name())
            .networkStatus(FAKER.options().option("In-Network", "Out-of-Network"))
            .build();
        
        return insuranceProvider;
    }    
    
    public static InsuranceProvider generate(DatabaseClient dbClient) {
        InsuranceProvider insuranceProvider = generate();
        log.trace("insuranceProvider {}", insert(dbClient, insuranceProvider).subscribe());
        return insuranceProvider;
    }    
    
    public static Mono<Long> insert(DatabaseClient dbClient, InsuranceProvider insuranceProvider) {
        return dbClient.sql("""
            INSERT INTO insuranceProviders (id, provider_name, specialty, network_status) 
                    VALUES (:id, :providerName, :specialty, :networkStatus) 
            """)
            .bind("id", insuranceProvider.getId())
            .bind("providerName", insuranceProvider.getProviderName())
            .bind("specialty", insuranceProvider.getSpecialty())
            .bind("networkStatus", insuranceProvider.getNetworkStatus())
            .fetch()
            .rowsUpdated();
    }           
}
