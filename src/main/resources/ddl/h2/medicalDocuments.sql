CREATE TABLE IF NOT EXISTS medicalDocuments (
    id VARCHAR(36) PRIMARY KEY,
    title TEXT NOT NULL,
    text_content TEXT NOT NULL,
    specialty TEXT,
    document_type TEXT,
    publication_date DATE,
    embeddding vector(1536)
)

