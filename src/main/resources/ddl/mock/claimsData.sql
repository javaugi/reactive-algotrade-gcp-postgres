CREATE TABLE IF NOT EXISTS claimsData (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    insurance_plan_id VARCHAR(36) REFERENCES insurancePlans(id),
    member_id VARCHAR(36) REFERENCES members(id),
    provider_id  VARCHAR(36) REFERENCES insuranceProviders(id),
    billed_amount DECIMAL(10,2),
    allowed_amount DECIMAL(10,2),
    paid_amount DECIMAL(10,2),
    diagnosis_codes VARCHAR(50),
    procedure_codes VARCHAR(50),
    claim_status VARCHAR(50) NOT NULL CHECK (claim_status IN ('SUBMITTED', 'RE-SUBMITTED', 'PROCESSED', 'IN-PROGRESS', 'PENDING', 'REJECTED', 'PAID')),
    service_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    claim_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
