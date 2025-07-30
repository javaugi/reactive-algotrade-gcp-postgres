CREATE TABLE IF NOT EXISTS pharmacies (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    pharmacy_code VARCHAR(20),
    name VARCHAR(100),
    email VARCHAR(200),
    phone VARCHAR(50),
    address VARCHAR(200),
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
)
