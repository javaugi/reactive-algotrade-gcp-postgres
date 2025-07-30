CREATE TABLE medications (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50),
    description BYTEA,
    dosage VARCHAR(128),
    unit VARCHAR(20),
    created_date DATE,
    updated_date DATE
)
