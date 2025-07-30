CREATE TABLE insurancePlans (
    id VARCHAR(36) PRIMARY KEY,
    insuranceCompanyId VARCHAR(36),
    planName VARCHAR(100) NOT NULL,
    planType VARCHAR(100) NOT NULL CHECK (planType IN ('HMO', 'PPO', 'EPO', 'POS', 'HDHP')),
    networkType VARCHAR(50) NOT NULL CHECK (networkType IN ('In-Network', 'Out-of-Network')),
    tierLevel VARCHAR(100) NOT NULL CHECK (tierLevel IN ('Bronze', 'Silver', 'Gold', 'Platinum', 'Catastrophic')),
    active BOOLEAN DEFAULT TRUE,
    effectiveDate DATE,
    expirationDate DATE,
    createdDate DATE,   
    updatedDate DATE,
    FOREIGN KEY (insuranceCompanyId) REFERENCES insuranceCompanies(id)
)
