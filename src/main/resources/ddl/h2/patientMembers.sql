CREATE TABLE patientMembers (
    id VARCHAR(36) PRIMARY KEY,
    patientId VARCHAR(36),
    memberId VARCHAR(36),
    FOREIGN KEY (patientId) REFERENCES patients(id),
    FOREIGN KEY (memberId) REFERENCES members(id)
)
