/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.controller.aiml;

import com.sisllc.instaiml.dto.aiml.MedicalDocumentMetadata;
import com.sisllc.instaiml.model.aiml.MedicalDocument;
import com.sisllc.instaiml.service.aiml.DocumentEmbeddingService;
import com.sisllc.instaiml.service.aiml.MedicalDocumentRagService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/medidoc")
@RequiredArgsConstructor
public class MedicalDocumentRagController {

    private final MedicalDocumentRagService ragService;
    private final DocumentEmbeddingService embeddingService;

    @PostMapping("/ask")
    public Mono<String> askMedicalQuestion(@RequestBody String question) {
        return ragService.answerMedicalQuestion(question);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadMedicalDocument(
        @RequestPart("file") FilePart file,
        @RequestPart("metadata") MedicalDocumentMetadata metadata) {

        return file.content()
            .collectList()
            .flatMap(dataBuffers -> {
                byte[] bytes = dataBuffers.stream()
                    .map(buffer -> {
                        byte[] array = new byte[buffer.readableByteCount()];
                        buffer.read(array);
                        DataBufferUtils.release(buffer);
                        return array;
                    })
                    .reduce(new byte[0], (a, b) -> {
                        byte[] combined = new byte[a.length + b.length];
                        System.arraycopy(a, 0, combined, 0, a.length);
                        System.arraycopy(b, 0, combined, a.length, b.length);
                        return combined;
                    });

            MedicalDocument document = MedicalDocument.builder()
                .pdfContent(bytes)
                .title(metadata.getTitle())
                .specialty(metadata.getSpecialty())
                .documentType(metadata.getDocumentType())
                .publicationDate(metadata.getPublicationDate()).build();

                return embeddingService.embedAndStoreMedicalDocument(document);
            });
    }
}
