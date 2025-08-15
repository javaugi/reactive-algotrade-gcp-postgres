CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS medicalDocuments (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    title TEXT NOT NULL,
    text_content TEXT NOT NULL,
    specialty TEXT,
    document_type TEXT,
    publication_date DATE,
    embeddding vector(1536)
);

-- Create index for better performance
CREATE INDEX IF NOT EXISTS med_docs_embedding_idx 
ON medicalDocuments USING ivfflat (embeddding vector_l2_ops) WITH (lists = 100);
