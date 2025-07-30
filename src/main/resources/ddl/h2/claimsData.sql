CREATE TABLE claimsData (
    id VARCHAR(36) PRIMARY KEY,
    insurancePlanId VARCHAR(36),
    memberId VARCHAR(36),
    providerId VARCHAR(36),
    billedAmount DECIMAL(10,2),
    allowedAmount DECIMAL(10,2),
    paidAmount DECIMAL(10,2),
    diagnosisCodes VARCHAR(20),
    procedureCodes VARCHAR(20),
    claimStatus VARCHAR(20) NOT NULL CHECK (claimStatus IN ('SUBMITTED', 'RE-SUBMITTED', 'PROCESSED', 'IN-PROGRESS', 'PENDING', 'REJECTED', 'PAID')),
    serviceDate DATE NOT NULL,
    claimDate DATE NOT NULL,
    createdDate DATE NOT NULL,
    updatedDate DATE,
    FOREIGN KEY (insurancePlanId) REFERENCES insurancePlans(id),
    FOREIGN KEY (memberId) REFERENCES members(id),
    FOREIGN KEY (providerId) REFERENCES insuranceProviders(id)
)
