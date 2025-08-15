/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.data;

import com.sisllc.instaiml.model.InsuranceCompany;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Slf4j
public class InsuranceCompanyGenerator extends DataGeneratorBase {

    public static InsuranceCompany generate() {
        InsuranceCompany insuranceCompany = InsuranceCompany.builder()
            .id(UUID.randomUUID().toString())
            .companyCode("CC-" + FAKER.number().digits(6))
            .companyName(FAKER.company().name())
            .stateLicenses("LIC-" + FAKER.number().digits(6))
            .financialRating("RATE-" + FAKER.financialTerms().noun())
            .contactInfo(FAKER.address().fullAddress())
            .marketShare(new BigDecimal(FAKER.number().randomDouble(2, 0, 1)))
            .build();
        
        return insuranceCompany;
    }    
    
    public static InsuranceCompany generate(DatabaseClient dbClient) {
        InsuranceCompany insuranceCompany = generate();
        log.trace("insuranceCompany {}", insert(dbClient, insuranceCompany).subscribe());
        return insuranceCompany;
    }    
    
    public static Mono<Long> insert(DatabaseClient dbClient, InsuranceCompany insuranceCompany) {
        return dbClient.sql("""
            INSERT INTO insuranceCompanies (id, company_code, company_name, state_licenses, financial_rating, contact_info, market_share) 
                    VALUES (:id, :companyCode, :companyName, :stateLicenses, :financialRating, :contactInfo, :marketShare) 
            """)
            .bind("id", insuranceCompany.getId())
            .bind("companyCode", insuranceCompany.getCompanyCode())
            .bind("companyName", insuranceCompany.getCompanyName())
            .bind("stateLicenses", insuranceCompany.getStateLicenses())
            .bind("financialRating", insuranceCompany.getFinancialRating())
            .bind("contactInfo", insuranceCompany.getContactInfo())
            .bind("marketShare", insuranceCompany.getMarketShare())
            .fetch()
            .rowsUpdated();
    }         
}
