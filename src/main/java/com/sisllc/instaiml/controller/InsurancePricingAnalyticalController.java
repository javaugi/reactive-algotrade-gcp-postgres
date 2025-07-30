/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.controller;

import com.sisllc.instaiml.dto.InsurancePricingDto;
import com.sisllc.instaiml.service.InsurancePricingAnalyticalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@RestController //Default for @RestController: JSON in/out
@RequestMapping(path = "/api/inspricing")
public class InsurancePricingAnalyticalController {
    private final InsurancePricingAnalyticalService analyticalService;
    
    @GetMapping(path = "costcoverage", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Flux<InsurancePricingDto> costVsCoverageAnalysis() {
       return analyticalService.costVsCoverageAnalysis();
    }    
    
    @GetMapping(path = "/benchmark", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Flux<InsurancePricingDto> marketBenchmarkingAnalysis() {
       return analyticalService.marketBenchmarkingAnalysis();
    }    
    
    @GetMapping(path = "/network", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Flux<InsurancePricingDto> networkAdequacyImpactAnalysis() {
       return analyticalService.networkAdequacyImpactAnalysis();
    }    
    
    @GetMapping(path = "/premVsPlan", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Flux<InsurancePricingDto> premiumByPlanTypeTierAnalysis() {
       return analyticalService.premiumByPlanTypeTierAnalysis();
    }    
    
    @GetMapping(path = "/premVsAge", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Flux<InsurancePricingDto> premiumVsAgeAnalysis() {
       return analyticalService.premiumVsAgeAnalysis();
    }    
    
    @GetMapping(path = "/tobacco", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Flux<InsurancePricingDto> tobaccoSurchargeImpactAnalysis() {
       return analyticalService.tobaccoSurchargeImpactAnalysis();
    }    

    @GetMapping(path = "/premVsAgeSimple", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Flux<InsurancePricingDto> premiumVsAgeSimpleAnalysis() {
       return analyticalService.premiumVsAgeSimpleAnalysis();
    }    
    
}
