/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.repository;

import com.sisllc.instaiml.model.InsurancePlan;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import com.sisllc.instaiml.dto.InsurancePricingDto;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

@Repository
public interface InsurancePricingRepository extends ReactiveCrudRepository<InsurancePlan, String> {
    // Or using a Projection Interface (Requires Spring Data Projections)
    // You would define an interface like:
    // public interface ClientNameEmail { String getName(); String getEmail(); }
    //@Query("SELECT p.planType, p.tierLevel FROM insurancePlans p")
    //@Query("${postgres.queries.testQuery}")
    @Query(InsurancePricingQueries.QUERY_PREM_BY_PLAN_TIER)
    Flux<InsurancePricingDto> premiumByPlanTypeTierAnalysis();

    @Query(InsurancePricingQueries.QUERY_COST_COVERAGE)
    Flux<InsurancePricingDto> costVsCoverageAnalysis();

    @Query(InsurancePricingQueries.QUERY_TOBACCO_SURCHARGE)
    Flux<InsurancePricingDto> tobaccoSurchargeImpactAnalysis();

    @Query(InsurancePricingQueries.QUERY_MARKET_BENCHMARKING)
    Flux<InsurancePricingDto> marketBenchmarkingAnalysis();

    @Query(InsurancePricingQueries.QUERY_PREMIUM_AGE)
    Flux<InsurancePricingDto> premiumVsAgeAnalysis();     
    
    @Query(InsurancePricingQueries.QUERY_RISK_POOL)
    Flux<InsurancePricingDto> riskPoolAnalysis();

    @Query(InsurancePricingQueries.QUERY_NETWORK_ADEQUACY)
    Flux<InsurancePricingDto> networkAdequacyImpactAnalysis();

    @Query(InsurancePricingQueries.QUERY_PREMIUM_AGE_SIMPLE)
    Flux<InsurancePricingDto> premiumVsAgeSimpleAnalysis();
}
