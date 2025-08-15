/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.model.aiml;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder(toBuilder = true)
@Table("medicalDocuments")
public class MedicalDocument {

    @Id
    private String id;

    private String title;

    @Column("text_content")
    private String textContent;

    private String specialty;

    @Column("document_type")
    private String documentType;

    @Column("publication_date")
    private OffsetDateTime publicationDate;

    @Column("embedding")
    private float[] embedding;

    // For file content, we'll handle separately
    @Transient
    @Column("pdf_content")
    private byte[] pdfContent;

    public void setEmbeddingFromList(List<Double> embeddingList) {
        this.embedding = new float[embeddingList.size()];
        for (int i = 0; i < embeddingList.size(); i++) {
            this.embedding[i] = embeddingList.get(i).floatValue();
        }
    }
}
