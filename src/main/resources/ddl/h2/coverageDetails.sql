CREATE TABLE coverageDetails (
    id VARCHAR(36) PRIMARY KEY,
    insurancePlanId VARCHAR(36),
    deductibleIndividual DECIMAL(10,2),
    deductibleFamily DECIMAL(10,2),
    oopMaxIndividual DECIMAL(10,2),
    oopMaxFamily DECIMAL(10,2),
    primaryCareCopay DECIMAL(6,2),
    specialistCopay DECIMAL(6,2),
    erCopay DECIMAL(6,2),
    prescriptionTier1 DECIMAL(6,2),
    prescriptionTier2 DECIMAL(6,2),
    prescriptionTier3 DECIMAL(6,2),
    effectiveDate DATE NOT NULL,
    expirationDate DATE,
    FOREIGN KEY (insurancePlanId) REFERENCES insurancePlans(id)
)
