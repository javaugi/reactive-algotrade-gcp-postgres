CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    username VARCHAR(50),
    password VARCHAR(256),
    roles VARCHAR(256),
    email VARCHAR(128),
    phone VARCHAR(20),
    firstName VARCHAR(50),
    lastName VARCHAR(50),
    city VARCHAR(50),
    age INT
);

CREATE TABLE IF NOT EXISTS patients (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    name VARCHAR(50),
    birthDate DATE,
    email VARCHAR(128),
    phone VARCHAR(20),
    address VARCHAR(128),
    createdDate DATE,
    updatedDate DATE
);

CREATE TABLE IF NOT EXISTS medications (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    name VARCHAR(50),
    description BYTEA,
    dosage VARCHAR(128),
    unit VARCHAR(20),
    createdDate DATE,
    updatedDate DATE
);

CREATE TABLE IF NOT EXISTS physicians (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    name VARCHAR(50),
    specialty VARCHAR(20),
    email VARCHAR(128),
    phone VARCHAR(20),
    address VARCHAR(128),
    createdDate DATE,
    updatedDate DATE
);

CREATE TABLE IF NOT EXISTS pharmacies (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    pharmacyCode VARCHAR(20),
    name VARCHAR(50),
    email VARCHAR(128),
    phone VARCHAR(20),
    address VARCHAR(128),
    createdDate DATE,
    updatedDate DATE
);

CREATE TABLE IF NOT EXISTS drugInventories (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    pharmacyId VARCHAR(36) REFERENCES pharmacies(id),
    medicationId VARCHAR(36) REFERENCES medications(id),
    quantity INT,
    reorderThreshold INT,
    createdDate DATE,
    updatedDate DATE
);

CREATE TABLE IF NOT EXISTS prescriptions (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    patientId VARCHAR(36) REFERENCES patients(id),
    physicianId VARCHAR(36) REFERENCES physicians(id),
    medicationId VARCHAR(36) REFERENCES medications(id),
    pharmacyId VARCHAR(36) REFERENCES pharmacies(id),
    quantity INT,
    refills INT,
    status VARCHAR(36) NOT NULL CHECK (status IN ('active', 'filled', 'canelled'))
    fillDate DATE,
    prescriptionDate DATE,
    createdDate DATE,
    updatedDate DATE
);


CREATE TABLE IF NOT EXISTS insuranceCompanies (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    companyCode VARCHAR(20) NOT NULL,
    companyName VARCHAR(50) NOT NULL,
    stateLicenses VARCHAR(20),
    financialRating VARCHAR(20),
    contactInfo VARCHAR(50),
    marketShare DECIMAL(8,2)
);

CREATE TABLE IF NOT EXISTS  insuranceProviders (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    providerName VARCHAR(50) NOT NULL,
    specialty VARCHAR(50),
    networkStatus VARCHAR(50) CHECK (network_status IN ('In-Network', 'Out-of-Network'))
);

CREATE TABLE IF NOT EXISTS insurancePlans (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    insuranceCompanyId VARCHAR(36) REFERENCES insuranceCompanies(id),
    planName VARCHAR(100) NOT NULL,
    planType VARCHAR(100) NOT NULL CHECK (planType IN ('HMO', 'PPO', 'EPO', 'POS', 'HDHP')),
    networkType VARCHAR(50) NOT NULL CHECK (networkType IN ('In-Network', 'Out-of-Network')),
    tierLevel VARCHAR(100) NOT NULL CHECK (tierLevel IN ('Bronze', 'Silver', 'Gold', 'Platinum', 'Catastrophic')),
    active BOOLEAN DEFAULT TRUE,
    effectiveDate DATE,
    expirationDate DATE,
    createdDate DATE,   
    updatedDate DATE
);

CREATE TABLE IF NOT EXISTS members (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    insurancePlanId VARCHAR(36) REFERENCES insurancePlans(id),
    name VARCHAR(50),
    gender VARCHAR(10),
    address VARCHAR(128),
    birthDate DATE,
    enrollmentDate DATE,
    terminationDate DATE,
    tobaccoUser BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS patientMembers (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    patientId VARCHAR(36) REFERENCES patients(id),
    memberId VARCHAR(36) REFERENCES members(id),
);

CREATE TABLE planPricings (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    insurancePlanId VARCHAR(36) REFERENCES insurancePlans(id),
    pricingType VARCHAR(100) NOT NULL CHECK (pricingType IN ('premium', 'deductible', 'copay', 'coinsuance', 'adjustment')),
    basePremium DECIMAL(10,2) NOT NULL,
    tobaccoSurcharge DECIMAL(10,2) DEFAULT 0,
    familyCoverageAdjustment DECIMAL(5,2) DEFAULT 0,
    miscAdjustment DECIMAL(5,2) DEFAULT 0,
    ageBracket VARCHAR(20) NOT NULL,
    coverageLevel NOT NULL CHECK (coverageLevel IN ('Bronze', 'Silver', 'Gold', 'Platinum', 'Catastrophic')),
    effectiveDate DATE NOT NULL,
    expirationDate DATE
);

CREATE TABLE coverageDetails (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    insurancePlanId VARCHAR(36) REFERENCES insurancePlans(id),
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
    expirationDate DATE
);

CREATE TABLE geographicPricings (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    insurancePlanId VARCHAR(36) REFERENCES insurancePlans(id),
    zipCode VARCHAR(10),
    stateAbbr VARCHAR(2),
    adjustmentFactor DECIMAL(5,2) DEFAULT 1.0,
    ratingArea INT,
    effectiveDate DATE NOT NULL,
    expirationDate DATE
);

CREATE TABLE claimsData (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    insurancePlanId VARCHAR(36) REFERENCES insurancePlans(id),
    memberId  VARCHAR(36) REFERENCES members(id),
    providerId  VARCHAR(36) REFERENCES insuranceProviders(id),
    billedAmount DECIMAL(10,2),
    allowedAmount DECIMAL(10,2),
    paidAmount DECIMAL(10,2),
    diagnosisCodes VARCHAR(20),
    procedureCodes VARCHAR(20),
    claimStatus VARCHAR(20) NOT NULL CHECK (coverageLevel IN ('SUBMITTED', 'RE-SUBMITTED', 'PROCESSED', 'IN-PROGRESS', 'PENDING', 'REJECTED', 'PAID')),
    serviceDate DATE NOT NULL,
    claimDate DATE NOT NULL,
    createdDate DATE NOT NULL,
    updatedDate DATE
);


