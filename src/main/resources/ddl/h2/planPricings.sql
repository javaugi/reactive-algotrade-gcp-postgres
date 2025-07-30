CREATE TABLE planPricings (
    id VARCHAR(36) PRIMARY KEY,
    insurancePlanId VARCHAR(36),
    pricingType VARCHAR(100) NOT NULL CHECK (pricingType IN ('premium', 'deductible', 'copay', 'coinsuance', 'adjustment')),
    basePremium DECIMAL(10,2) NOT NULL,
    tobaccoSurcharge DECIMAL(10,2) DEFAULT 0,
    familyCoverageAdjustment DECIMAL(5,2) DEFAULT 0,
    miscAdjustment DECIMAL(5,2) DEFAULT 0,
    ageBracket VARCHAR(20) NOT NULL,
    coverageLevel VARCHAR(20) NOT NULL CHECK (coverageLevel IN ('Bronze', 'Silver', 'Gold', 'Platinum', 'Catastrophic')),
    effectiveDate DATE NOT NULL,
    expirationDate DATE,
    FOREIGN KEY (insurancePlanId) REFERENCES insurancePlans(id)
)
