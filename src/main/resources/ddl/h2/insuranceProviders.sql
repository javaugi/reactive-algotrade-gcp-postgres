CREATE TABLE insuranceProviders (
    id VARCHAR(36) PRIMARY KEY,
    providerName VARCHAR(50) NOT NULL,
    specialty VARCHAR(50),
    networkStatus VARCHAR(50) CHECK (networkStatus IN ('In-Network', 'Out-of-Network'))
)
