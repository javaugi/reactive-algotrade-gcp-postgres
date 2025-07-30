DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(256),
    roles VARCHAR(256),
    email VARCHAR(128),
    phone VARCHAR(50),
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    city VARCHAR(50),
    age INT
)
;
