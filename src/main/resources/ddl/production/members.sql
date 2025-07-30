CREATE TABLE IF NOT EXISTS members (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    insurance_plan_id VARCHAR(36) REFERENCES insurancePlans(id),
    name VARCHAR(100),
    gender VARCHAR(50),
    address VARCHAR(128),
    tobacco_user BOOLEAN DEFAULT FALSE,
    birth_date TIMESTAMP WITH TIME ZONE,
    enrollment_date TIMESTAMP WITH TIME ZONE,
    termination_date TIMESTAMP WITH TIME ZONE
)
