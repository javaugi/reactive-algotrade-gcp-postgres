CREATE TABLE IF NOT EXISTS insurancePlans (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    insurance_company_id VARCHAR(36) REFERENCES insuranceCompanies(id),
    plan_name VARCHAR(100) NOT NULL,
    plan_type VARCHAR(100) NOT NULL CHECK (plan_type IN ('HMO', 'PPO', 'EPO', 'POS', 'HDHP')),
    network_type VARCHAR(50) NOT NULL CHECK (network_type IN ('In-Network', 'Out-of-Network')),
    tier_level VARCHAR(100) NOT NULL CHECK (tier_level IN ('Bronze', 'Silver', 'Gold', 'Platinum', 'Catastrophic')),
    active BOOLEAN DEFAULT TRUE,
    effective_date TIMESTAMP WITH TIME ZONE,
    expiration_date TIMESTAMP WITH TIME ZONE,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,   
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
)
