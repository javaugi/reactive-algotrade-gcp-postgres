CREATE TABLE IF NOT EXISTS prescriptions (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    patient_id VARCHAR(36) REFERENCES patients(id),
    physician_id VARCHAR(36) REFERENCES physicians(id),
    medication_id VARCHAR(36) REFERENCES medications(id),
    pharmacy_id VARCHAR(36) REFERENCES pharmacies(id),
    quantity INT,
    refills INT,
    status VARCHAR(50) NOT NULL CHECK (status IN ('active', 'expired', 'filled', 'canelled')),
    fill_date TIMESTAMP WITH TIME ZONE,
    prescription_date TIMESTAMP WITH TIME ZONE,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
)
