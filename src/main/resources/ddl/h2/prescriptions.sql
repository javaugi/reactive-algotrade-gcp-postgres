CREATE TABLE prescriptions (
    id VARCHAR(36) PRIMARY KEY,
    patientId VARCHAR(36),
    physicianId VARCHAR(36),
    medicationId VARCHAR(36),
    pharmacyId VARCHAR(36),
    quantity INT,
    refills INT,
    status VARCHAR(36) NOT NULL CHECK (status IN ('active', 'expired', 'filled', 'canelled')),
    fillDate DATE,
    prescriptionDate DATE,
    createdDate DATE,
    updatedDate DATE,
    FOREIGN KEY (patientId) REFERENCES patients(id),
    FOREIGN KEY (physicianId) REFERENCES physicians(id),
    FOREIGN KEY (medicationId) REFERENCES medications(id),
    FOREIGN KEY (pharmacyId) REFERENCES pharmacies(id)
)
