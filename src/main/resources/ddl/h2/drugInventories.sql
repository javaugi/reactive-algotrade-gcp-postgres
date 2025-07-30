CREATE TABLE drugInventories (
    id VARCHAR(36) PRIMARY KEY,
    pharmacy_id VARCHAR(36),
    medication_id VARCHAR(36),
    quantity INT,
    reorder_threshold INT,
    created_date DATE,
    updated_date DATE,
    FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id),
    FOREIGN KEY (medication_id) REFERENCES medications(id)
)
