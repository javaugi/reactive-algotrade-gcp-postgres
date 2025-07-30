/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@ConfigurationProperties(prefix = "postgres.queries")
@Component("postgresQueryConfig")
public class PostgresQueryConfig { 
    private String premiumByPlanTypeTierAnalysis;
    private String costVsCoverageAnalysis;
    private String tobaccoSurchargeImpactAnalysis;
    private String marketBenchmarkingAnalysis;
    private String premiumVsAgeAnalysis;
    private String riskPoolAnalysis;
    private String networkAdequacyImpactAnalysis;
    
    public String getQuery(String queryName) {
        return switch (queryName) {
            case KEY_PREM_BY_PLAN_TIER -> premiumByPlanTypeTierAnalysis;
            case KEY_COST_COVERAGE -> costVsCoverageAnalysis;
            case KEY_TOBACCO_SURCHARGE -> tobaccoSurchargeImpactAnalysis;
            case KEY_MARKET_BENCHMARKING -> marketBenchmarkingAnalysis;
            case KEY_PREMIUM_AGE -> premiumVsAgeAnalysis;
            case KEY_RISK_POOL -> riskPoolAnalysis;
            case KEY_NETWORK_ADEQUACY -> networkAdequacyImpactAnalysis;
            default -> null;
        }; // Or throw an exception
    }
    
    public static final String KEY_PREM_BY_PLAN_TIER = "premiumByPlanTypeTierAnalysis";
    public static final String KEY_COST_COVERAGE = "costVsCoverageAnalysis";
    public static final String KEY_TOBACCO_SURCHARGE = "tobaccoSurchargeImpactAnalysis";
    public static final String KEY_MARKET_BENCHMARKING = "marketBenchmarkingAnalysis";
    public static final String KEY_PREMIUM_AGE = "premiumVsAgeAnalysis";
    public static final String KEY_RISK_POOL = "riskPoolAnalysis";
    public static final String KEY_NETWORK_ADEQUACY = "networkAdequacyImpactAnalysis";
}

/*
Key Differences:
Feature             ${...}Syntax  (Property Injection)              #{...} Syntax (Spring Expression Language (SpEL))
       @Query("${cosmos.queries.premiumByPlanTypeTierAnalysis}")    @Query("#{@cosmosQueryConfig.premiumByPlanTypeTierAnalysis}")
Configuration       Needs only @PropertySource                      Requires @ConfigurationProperties
Type Safety         No (pure string injection)                      Yes (bound to Java fields)
Flexibility         Simple properties only                          Can use complex SpEL expressions
Error Detection     Runtime errors only                             Compile-time field validation
*/