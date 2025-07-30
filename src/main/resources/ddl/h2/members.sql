CREATE TABLE members (
    id VARCHAR(36) PRIMARY KEY,
    insurancePlanId VARCHAR(36),
    name VARCHAR(50),
    gender VARCHAR(20),
    address VARCHAR(128),
    birthDate DATE,
    enrollmentDate DATE,
    terminationDate DATE,
    tobaccoUser BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (insurancePlanId) REFERENCES insurancePlans(id)
)
