CREATE TABLE insuranceCompanies (
    id VARCHAR(36) PRIMARY KEY,
    companyCode VARCHAR(20) NOT NULL,
    companyName VARCHAR(50) NOT NULL,
    stateLicenses VARCHAR(20),
    financialRating VARCHAR(20),
    contactInfo VARCHAR(50),
    marketShare DECIMAL(8,2)
)
