CREATE TABLE IF NOT EXISTS patientMembers (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    patient_id VARCHAR(36) REFERENCES patients(id),
    member_id VARCHAR(36) REFERENCES members(id)
)
