CREATE TABLE IF NOT EXISTS coverageDetails (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    insurance_plan_id VARCHAR(36) REFERENCES insurancePlans(id),
    deductible_individual DECIMAL(10,2),
    deductible_family DECIMAL(10,2),
    oop_max_individual DECIMAL(10,2),
    oop_max_family DECIMAL(10,2),
    primary_care_copay DECIMAL(6,2),
    specialist_copay DECIMAL(6,2),
    er_copay DECIMAL(6,2),
    prescription_tier1 DECIMAL(6,2),
    prescription_tier2 DECIMAL(6,2),
    prescription_tier3 DECIMAL(6,2),
    effective_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expiration_date TIMESTAMP WITH TIME ZONE
)
