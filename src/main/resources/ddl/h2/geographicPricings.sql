CREATE TABLE geographicPricings (
    id VARCHAR(36) PRIMARY KEY,
    insurancePlanId VARCHAR(36),
    zipCode VARCHAR(20),
    stateAbbr VARCHAR(30),
    adjustmentFactor DECIMAL(5,2) DEFAULT 1.0,
    ratingArea INT,
    effectiveDate DATE NOT NULL,
    expirationDate DATE,
    FOREIGN KEY (insurancePlanId) REFERENCES insurancePlans(id)
)
