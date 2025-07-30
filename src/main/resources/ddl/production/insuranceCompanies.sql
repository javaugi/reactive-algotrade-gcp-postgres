CREATE TABLE IF NOT EXISTS insuranceCompanies (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    company_code VARCHAR(50) NOT NULL,
    company_name VARCHAR(50) NOT NULL,
    state_licenses VARCHAR(50),
    financial_rating VARCHAR(50),
    contact_info VARCHAR(100),
    market_share DECIMAL(8,2)
)
