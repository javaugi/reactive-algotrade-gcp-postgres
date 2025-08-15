/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.service.aiml;

import com.sisllc.instaiml.model.aiml.MedicalDocument;
import com.sisllc.instaiml.repository.aiml.MedicalDocumentRepository;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class DocumentEmbeddingService {

    private final OpenAiService openAiService;
    private final MedicalDocumentRepository documentRepository;

    public Mono<String> embedAndStoreMedicalDocument(MedicalDocument document) {
        return Mono.fromCallable(() -> extractTextFromPdf(document.getPdfContent()))
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(text -> {
                document.setTextContent(text);
                return generateEmbedding(text)
                    .flatMap(embedding -> {
                        document.setEmbeddingFromList(embedding);
                        return saveDocumentToDatabase(document);
                    });
            });
    }

    private Mono<List<Double>> generateEmbedding(String text) {
        return Mono.fromCallable(() -> {
            EmbeddingRequest request = EmbeddingRequest.builder()
                .model("text-embedding-ada-002")
                .input(List.of(text))
                .build();

            return openAiService.createEmbeddings(request)
                .getData()
                .get(0)
                .getEmbedding();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<String> saveDocumentToDatabase(MedicalDocument document) {
        return documentRepository.saveDocument(
            document.getTitle(),
            document.getTextContent(),
            document.getSpecialty(),
            document.getDocumentType(),
            document.getPublicationDate(),
            document.getEmbedding()
        ).thenReturn("Document stored successfully");
    }

    private String extractTextFromPdf(byte[] pdfBytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
