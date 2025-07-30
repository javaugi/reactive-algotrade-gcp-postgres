CREATE TABLE IF NOT EXISTS geographicPricings (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    insurance_plan_id VARCHAR(36) REFERENCES insurancePlans(id),
    zip_code VARCHAR(20),
    state_abbr VARCHAR(20),
    adjustment_factor DECIMAL(5,2) DEFAULT 1.0,
    rating_area INT,
    effective_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expiration_date TIMESTAMP WITH TIME ZONE
)
