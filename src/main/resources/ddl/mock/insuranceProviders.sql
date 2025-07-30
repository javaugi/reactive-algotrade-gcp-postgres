CREATE TABLE IF NOT EXISTS insuranceProviders (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    provider_name VARCHAR(50) NOT NULL,
    specialty VARCHAR(50),
    network_status VARCHAR(50) CHECK (network_status IN ('In-Network', 'Out-of-Network'))
)
