CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    name VARCHAR(100),
    username VARCHAR(50),
    password VARCHAR(256),
    roles VARCHAR(256),
    email VARCHAR(200),
    phone VARCHAR(50),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    city VARCHAR(50),
    age INT,
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
)


