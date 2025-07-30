CREATE TABLE IF NOT EXISTS drugInventories (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    pharmacy_id VARCHAR(36) REFERENCES pharmacies(id),
    medication_id VARCHAR(36) REFERENCES medications(id),
    quantity INT,
    reorder_threshold INT,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
)
