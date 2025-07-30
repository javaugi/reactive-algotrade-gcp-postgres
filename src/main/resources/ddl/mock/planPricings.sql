CREATE TABLE IF NOT EXISTS planPricings (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    insurance_plan_id VARCHAR(36) REFERENCES insurancePlans(id),
    pricing_type VARCHAR(100) NOT NULL CHECK (pricing_type IN ('premium', 'deductible', 'copay', 'coinsurance', 'adjustment')),
    base_premium DECIMAL(10,2) NOT NULL,
    tobacco_surcharge DECIMAL(10,2) DEFAULT 0,
    family_coverage_adjustment DECIMAL(5,2) DEFAULT 0,
    misc_adjustment DECIMAL(5,2) DEFAULT 0,
    age_bracket VARCHAR(50) NOT NULL,
    coverage_level VARCHAR(50) NOT NULL CHECK (coverage_level IN ('Bronze', 'Silver', 'Gold', 'Platinum', 'Catastrophic')),
    effective_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expiration_date TIMESTAMP WITH TIME ZONE
)
