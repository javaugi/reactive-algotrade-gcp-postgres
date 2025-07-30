CREATE TABLE pharmacies (
    id VARCHAR(36) PRIMARY KEY,
    pharmacy_code VARCHAR(20),
    name VARCHAR(50),
    email VARCHAR(128),
    phone VARCHAR(30),
    address VARCHAR(128),
    created_date DATE,
    updated_date DATE
)
