/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.service;

import com.sisllc.instaiml.dto.InsurancePricingDto;
import com.sisllc.instaiml.repository.InsurancePricingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@Service
public class InsurancePricingAnalyticalService {
    private final InsurancePricingRepository insurancePricingRepository;
    
    public void performAnalytics() {
        log.debug("performAnalytics entered ... ");
        doPremiumVsAgeAnalysis();
        log.debug("premiumVsAgeAnalysis Done ");
        doPremiumByPlanTypeTierAnalysis();
        log.debug("premiumByPlanTypeTierAnalysis Done.");
        doCostVsCoverageAnalysis();
        log.debug("costVsCoverageAnalysis Done.");
        doTobaccoSurchargeImpactAnalysis();
        log.debug("tobaccoSurchargeImpactAnalysis Done.");
        doMarketBenchmarkingAnalysis();
        log.debug("marketBenchmarkingAnalysis Done.");
        doRiskPoolAnalysis();
        log.debug("riskPoolAnalysis Done");
        doNetworkAdequacyImpactAnalysis();
        log.debug("doNetworkAdequacyImpactAnalysis Done");

        log.debug("##### doPremiumVsAgeSimpleAnalysis ...");
        doPremiumVsAgeSimpleAnalysis();
        log.debug("All Done performAnalytics after networkAdequacyImpactAnalysis");
    }
    
    private void doPremiumVsAgeSimpleAnalysis() {
        try{
            premiumVsAgeSimpleAnalysis()
                .doOnNext(dto -> log.debug("premiumVsAgeSimpleAnalysis {}", dto))
                .switchIfEmpty(dto -> log.debug("premiumVsAgeSimpleAnalysis: No Data Found"))
                .subscribe();
        }catch(Exception ex) {
            log.error(" Error premiumVsAgeSimpleAnalysis", ex);
        }
  
    }        

    public Flux<InsurancePricingDto> premiumVsAgeSimpleAnalysis() {
        return insurancePricingRepository.premiumVsAgeSimpleAnalysis();
    }    
    
    private void doPremiumVsAgeAnalysis() {
        try{
            premiumVsAgeAnalysis()
                .doOnNext(dto -> log.debug("premiumVsAgeAnalysis {}", dto))
                .switchIfEmpty(dto -> log.debug("premiumVsAgeAnalysis: No Data Found"))
                .subscribe();
        }catch(Exception ex) {
            log.error(" Error premiumVsAgeAnalysis", ex);
        }
  
    }        
    
    public Flux<InsurancePricingDto> premiumVsAgeAnalysis() {
        return insurancePricingRepository.premiumVsAgeAnalysis();
    }
    
    private void doPremiumByPlanTypeTierAnalysis() {
        try{
            premiumByPlanTypeTierAnalysis()
                .doOnNext(dto -> log.debug("premiumByPlanTypeTierAnalysis {}", dto))
                .switchIfEmpty(dto -> log.debug("premiumByPlanTypeTierAnalysis: No Data Found"))
                .subscribe();
        }catch(Exception ex) {
            log.error(" Error premiumByPlanTypeTierAnalysis", ex);
        }
    }
     
    public Flux<InsurancePricingDto> premiumByPlanTypeTierAnalysis() {
        return insurancePricingRepository.premiumByPlanTypeTierAnalysis();
    }

    private void doCostVsCoverageAnalysis() {
        try{
            premiumByPlanTypeTierAnalysis()
                .doOnNext(dto -> log.debug("costVsCoverageAnalysis {}", dto))
                .switchIfEmpty(dto -> log.debug("costVsCoverageAnalysis: No Data Found"))
                .subscribe();
        }catch(Exception ex) {
            log.error(" Error costVsCoverageAnalysis", ex);
        }
    }
     
    public Flux<InsurancePricingDto> costVsCoverageAnalysis() {
        return insurancePricingRepository.costVsCoverageAnalysis();
    }

    private void doTobaccoSurchargeImpactAnalysis() {
        try{
            tobaccoSurchargeImpactAnalysis()
                .doOnNext(dto -> log.debug("tobaccoSurchargeImpactAnalysis {}", dto))
                .switchIfEmpty(dto -> log.debug("tobaccoSurchargeImpactAnalysis: No Data Found"))
                .subscribe();
        }catch(Exception ex) {
            log.error(" Error tobaccoSurchargeImpactAnalysis", ex);
        }
    }    
         
    public Flux<InsurancePricingDto> tobaccoSurchargeImpactAnalysis() {
        return insurancePricingRepository.tobaccoSurchargeImpactAnalysis();
    }

    private void doMarketBenchmarkingAnalysis() {
        try{
            marketBenchmarkingAnalysis()
                .doOnNext(dto -> log.debug("marketBenchmarkingAnalysis {}", dto))
                .switchIfEmpty(dto -> log.debug("marketBenchmarkingAnalysis: No Data Found"))
                .subscribe();
        }catch(Exception ex) {
            log.error(" Error marketBenchmarkingAnalysis", ex);
        }
    }        
         
    public Flux<InsurancePricingDto> marketBenchmarkingAnalysis() {
        return insurancePricingRepository.marketBenchmarkingAnalysis();
    }

    private void doRiskPoolAnalysis() {
        try{
            riskPoolAnalysis()
                .doOnNext(dto -> log.debug("riskPoolAnalysis {}", dto))
                .switchIfEmpty(dto -> log.debug("riskPoolAnalysis: No Data Found"))
                .subscribe();
        }catch(Exception ex) {
            log.error(" Error riskPoolAnalysis", ex);
        }
    }       
        
    public Flux<InsurancePricingDto> riskPoolAnalysis() {
        return insurancePricingRepository.riskPoolAnalysis();
    }

    private void doNetworkAdequacyImpactAnalysis() {
        try{
            networkAdequacyImpactAnalysis()
                .doOnNext(dto -> log.debug("networkAdequacyImpactAnalysis {}", dto))
                .switchIfEmpty(dto -> log.debug("networkAdequacyImpactAnalysis: No Data Found"))
                .subscribe();
        }catch(Exception ex) {
            log.error(" Error premiumByPlanTypeTierAnalysis", ex);
        }
    }           
        
    public Flux<InsurancePricingDto> networkAdequacyImpactAnalysis() {
        return insurancePricingRepository.networkAdequacyImpactAnalysis();
    }
}

/*
Health Insurance Pricing Analysis Database Design
Here's a comprehensive database design for health insurance pricing analysis with example tables, relationships, and sample analytical queries.

1. Database Schema Design
Core Tables
sql
-- Insurance Plans
CREATE TABLE insurance_plans (
    plan_id SERIAL PRIMARY KEY,
    plan_name VARCHAR(100) NOT NULL,
    insurance_company_id INT REFERENCES insurance_companies(company_id),
    plan_type VARCHAR(50) CHECK (plan_type IN ('HMO', 'PPO', 'EPO', 'POS', 'HDHP')),
    metal_tier VARCHAR(20) CHECK (metal_tier IN ('Bronze', 'Silver', 'Gold', 'Platinum', 'Catastrophic')),
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insurance Companies
CREATE TABLE insurance_companies (
    company_id SERIAL PRIMARY KEY,
    company_name VARCHAR(100) NOT NULL,
    state_licensed VARCHAR(2),
    market_share DECIMAL(5,2),
    financial_rating VARCHAR(5)
);

-- Plan Pricing
CREATE TABLE plan_pricing (
    pricing_id SERIAL PRIMARY KEY,
    plan_id INT REFERENCES insurance_plans(plan_id),
    age_bracket VARCHAR(20) NOT NULL,
    base_premium DECIMAL(10,2) NOT NULL,
    tobacco_surcharge DECIMAL(10,2) DEFAULT 0,
    family_coverage_adjustment DECIMAL(5,2) DEFAULT 0,
    effective_date DATE NOT NULL,
    end_date DATE
);

-- Coverage Details
CREATE TABLE coverage_details (
    coverage_id SERIAL PRIMARY KEY,
    plan_id INT REFERENCES insurance_plans(plan_id),
    deductible_individual DECIMAL(10,2),
    deductible_family DECIMAL(10,2),
    oop_max_individual DECIMAL(10,2),
    oop_max_family DECIMAL(10,2),
    primary_care_copay DECIMAL(6,2),
    specialist_copay DECIMAL(6,2),
    er_copay DECIMAL(6,2),
    prescription_tier1 DECIMAL(6,2),
    prescription_tier2 DECIMAL(6,2),
    prescription_tier3 DECIMAL(6,2)
);

-- Geographic Pricing
CREATE TABLE geographic_pricing (
    geo_id SERIAL PRIMARY KEY,
    plan_id INT REFERENCES insurance_plans(plan_id),
    zip_code VARCHAR(10),
    county VARCHAR(100),
    rating_area INT,
    geographic_factor DECIMAL(5,2) DEFAULT 1.0
);
Supporting Tables
sql
-- Claims Data (for actual cost analysis)
CREATE TABLE medical_claims (
    claim_id SERIAL PRIMARY KEY,
    member_id INT REFERENCES members(member_id),
    plan_id INT REFERENCES insurance_plans(plan_id),
    provider_id INT REFERENCES providers(provider_id),
    service_date DATE NOT NULL,
    procedure_code VARCHAR(10),
    diagnosis_code VARCHAR(10),
    billed_amount DECIMAL(12,2),
    allowed_amount DECIMAL(12,2),
    paid_amount DECIMAL(12,2),
    claim_status VARCHAR(20)
);

-- Members
CREATE TABLE members (
    member_id SERIAL PRIMARY KEY,
    plan_id INT REFERENCES insurance_plans(plan_id),
    gender VARCHAR(10),
    birth_date DATE,
    tobacco_user BOOLEAN DEFAULT FALSE,
    enrollment_date DATE,
    termination_date DATE
);

-- Providers
CREATE TABLE providers (
    provider_id SERIAL PRIMARY KEY,
    provider_name VARCHAR(100) NOT NULL,
    specialty VARCHAR(100),
    network_status VARCHAR(20) CHECK (network_status IN ('In-Network', 'Out-of-Network'))
);
2. Sample Analytical Queries
1. Premium Comparison by Plan Type and Metal Tier
sql
SELECT 
    p.plan_type,
    p.metal_tier,
    AVG(pp.base_premium * g.geographic_factor) AS avg_adjusted_premium,
    MIN(pp.base_premium * g.geographic_factor) AS min_premium,
    MAX(pp.base_premium * g.geographic_factor) AS max_premium,
    COUNT(DISTINCT p.plan_id) AS plan_count
FROM 
    insurance_plans p
JOIN 
    plan_pricing pp ON p.plan_id = pp.plan_id
JOIN 
    geographic_pricing g ON p.plan_id = g.plan_id
WHERE 
    pp.age_bracket = '40-49'
    AND g.rating_area = 12
    AND p.is_active = TRUE
GROUP BY 
    p.plan_type, p.metal_tier
ORDER BY 
    avg_adjusted_premium;
2. Cost vs. Coverage Analysis
sql
SELECT 
    p.plan_id,
    p.plan_name,
    c.deductible_individual,
    c.oop_max_individual,
    pp.base_premium,
    (pp.base_premium * 12 + c.deductible_individual) AS estimated_annual_cost,
    ROUND(AVG(cl.allowed_amount) AS avg_claim_cost,
    COUNT(cl.claim_id) AS claim_count
FROM 
    insurance_plans p
JOIN 
    coverage_details c ON p.plan_id = c.plan_id
JOIN 
    plan_pricing pp ON p.plan_id = pp.plan_id
LEFT JOIN 
    medical_claims cl ON p.plan_id = cl.plan_id
WHERE 
    pp.age_bracket = '30-39'
    AND p.metal_tier = 'Silver'
GROUP BY 
    p.plan_id, p.plan_name, c.deductible_individual, c.oop_max_individual, pp.base_premium
ORDER BY 
    estimated_annual_cost;
3. Tobacco Surcharge Impact Analysis
sql
SELECT 
    p.plan_id,
    p.plan_name,
    ic.company_name,
    pp.age_bracket,
    pp.base_premium AS non_tobacco_premium,
    (pp.base_premium + pp.tobacco_surcharge) AS tobacco_premium,
    ROUND((pp.tobacco_surcharge / pp.base_premium) * 100, 2) AS surcharge_percentage,
    COUNT(m.member_id) FILTER (WHERE m.tobacco_user = TRUE) AS tobacco_users,
    COUNT(m.member_id) FILTER (WHERE m.tobacco_user = FALSE) AS non_tobacco_users
FROM 
    insurance_plans p
JOIN 
    plan_pricing pp ON p.plan_id = pp.plan_id
JOIN 
    insurance_companies ic ON p.insurance_company_id = ic.company_id
LEFT JOIN 
    members m ON p.plan_id = m.plan_id
GROUP BY 
    p.plan_id, p.plan_name, ic.company_name, pp.age_bracket, pp.base_premium, pp.tobacco_surcharge
ORDER BY 
    surcharge_percentage DESC;
3. Example Pricing Analysis Reports
Report 1: Market Benchmarking
sql
-- Compare premiums across companies for similar plans
SELECT 
    ic.company_name,
    p.metal_tier,
    p.plan_type,
    ROUND(AVG(pp.base_premium), 2) AS avg_base_premium,
    ROUND(AVG(pp.base_premium * g.geographic_factor), 2) AS avg_adjusted_premium,
    ic.market_share
FROM 
    insurance_plans p
JOIN 
    insurance_companies ic ON p.insurance_company_id = ic.company_id
JOIN 
    plan_pricing pp ON p.plan_id = pp.plan_id
JOIN 
    geographic_pricing g ON p.plan_id = g.plan_id
WHERE 
    pp.age_bracket = '40-49'
    AND g.rating_area = 5
    AND p.is_active = TRUE
GROUP BY 
    ic.company_name, p.metal_tier, p.plan_type, ic.market_share
ORDER BY 
    p.metal_tier, avg_adjusted_premium;
Report 2: Age-Based Pricing Curve
sql
-- Show how premiums increase with age
SELECT 
    pp.age_bracket,
    ROUND(AVG(pp.base_premium), 2) AS avg_premium,
    ROUND(AVG(pp.base_premium) - LAG(ROUND(AVG(pp.base_premium), 2)) OVER (ORDER BY MIN(age_sort_order)), 2) AS premium_increase,
    ROUND((AVG(pp.base_premium) - LAG(AVG(pp.base_premium)) OVER (ORDER BY MIN(age_sort_order))) / 
        LAG(AVG(pp.base_premium)) OVER (ORDER BY MIN(age_sort_order)) * 100, 2) AS percent_increase
FROM 
    plan_pricing pp
JOIN 
    (SELECT age_bracket, 
            CASE 
                WHEN age_bracket = '0-20' THEN 1
                WHEN age_bracket = '21-29' THEN 2
                WHEN age_bracket = '30-39' THEN 3
                WHEN age_bracket = '40-49' THEN 4
                WHEN age_bracket = '50-59' THEN 5
                ELSE 6 
            END AS age_sort_order
     FROM plan_pricing
     GROUP BY age_bracket) ages ON pp.age_bracket = ages.age_bracket
GROUP BY 
    pp.age_bracket, ages.age_sort_order
ORDER BY 
    ages.age_sort_order;
4. Advanced Analysis Ideas
Risk Pool Analysis:

sql
-- Compare actual claims costs to premiums collected
SELECT 
    p.plan_id,
    p.plan_name,
    COUNT(m.member_id) AS enrolled_members,
    SUM(pp.base_premium * 12) AS annual_premium_volume,
    SUM(cl.allowed_amount) AS annual_claims_costs,
    (SUM(pp.base_premium * 12) - SUM(cl.allowed_amount)) AS underwriting_result,
    ROUND((SUM(cl.allowed_amount) / SUM(pp.base_premium * 12)) * 100, 2) AS loss_ratio
FROM 
    insurance_plans p
JOIN 
    members m ON p.plan_id = m.plan_id
JOIN 
    plan_pricing pp ON p.plan_id = pp.plan_id AND 
    pp.age_bracket = CASE 
        WHEN EXTRACT(YEAR FROM AGE(m.birth_date)) BETWEEN 0 AND 20 THEN '0-20'
        WHEN EXTRACT(YEAR FROM AGE(m.birth_date)) BETWEEN 21 AND 29 THEN '21-29'
        WHEN EXTRACT(YEAR FROM AGE(m.birth_date)) BETWEEN 30 AND 39 THEN '30-39'
        WHEN EXTRACT(YEAR FROM AGE(m.birth_date)) BETWEEN 40 AND 49 THEN '40-49'
        ELSE '50+'
    END
LEFT JOIN 
    medical_claims cl ON p.plan_id = cl.plan_id AND 
    cl.service_date BETWEEN '2023-01-01' AND '2023-12-31'
WHERE 
    p.is_active = TRUE
GROUP BY 
    p.plan_id, p.plan_name
ORDER BY 
    loss_ratio DESC;
Network Adequacy Impact:

sql
-- Analyze how provider network affects costs
SELECT 
    p.plan_id,
    p.plan_name,
    COUNT(DISTINCT pr.provider_id) FILTER (WHERE pr.network_status = 'In-Network') AS in_network_providers,
    COUNT(DISTINCT cl.claim_id) FILTER (WHERE pr.network_status = 'In-Network') AS in_network_claims,
    COUNT(DISTINCT cl.claim_id) FILTER (WHERE pr.network_status = 'Out-of-Network') AS out_of_network_claims,
    ROUND(AVG(cl.allowed_amount) FILTER (WHERE pr.network_status = 'In-Network'), 2) AS avg_in_network_cost,
    ROUND(AVG(cl.allowed_amount) FILTER (WHERE pr.network_status = 'Out-of-Network'), 2) AS avg_out_of_network_cost
FROM 
    insurance_plans p
JOIN 
    medical_claims cl ON p.plan_id = cl.plan_id
JOIN 
    providers pr ON cl.provider_id = pr.provider_id
GROUP BY 
    p.plan_id, p.plan_name
ORDER BY 
    (avg_out_of_network_cost - avg_in_network_cost) DESC;
This database design and analysis framework allows you to:

Compare plans across multiple dimensions (price, coverage, provider network)

Analyze the relationship between premiums and actual healthcare costs

Identify pricing trends by age, geography, and plan characteristics

Evaluate the financial performance of different plans

Model the impact of different benefit designs on costs
*/