/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.service.aiml;

import com.sisllc.instaiml.model.aiml.MedicalDocument;
import com.sisllc.instaiml.repository.aiml.MedicalDocumentRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class MedicalDocumentRagService {
    private final MedicalDocumentRepository documentRepository;
    private final OpenAiService openAiService;

    public Mono<String> answerMedicalQuestion(String question) {
        return generateEmbedding(question)
            .flatMap(embedding -> documentRepository.findSimilarDocuments(embedding)
            .collectList()
            .flatMap(docs -> generateAnswer(question, docs))
            );
    }

    private Mono<float[]> generateEmbedding(String text) {
        return Mono.fromCallable(() -> {
            EmbeddingRequest request = EmbeddingRequest.builder()
                .model("text-embedding-ada-002")
                .input(List.of(text))
                .build();

            List<Double> embedding = openAiService.createEmbeddings(request)
                .getData()
                .get(0)
                .getEmbedding();

            float[] floatEmbedding = new float[embedding.size()];
            for (int i = 0; i < embedding.size(); i++) {
                floatEmbedding[i] = embedding.get(i).floatValue();
            }
            return floatEmbedding;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<String> generateAnswer(String question, List<MedicalDocument> documents) {
        return Mono.fromCallable(() -> {
            String context = documents.stream()
                .map(doc -> String.format("Title: %s\nSpecialty: %s\nContent: %s",
                doc.getTitle(), doc.getSpecialty(),
                doc.getTextContent().substring(0, Math.min(2000, doc.getTextContent().length()))))
                .collect(Collectors.joining("\n\n"));

            List<ChatMessage> messages = List.of(
                new ChatMessage(ChatMessageRole.SYSTEM.value(),
                    "You are a helpful medical AI assistant. Use the provided medical context to answer questions. "
                    + "If you don't know the answer, say you don't know. Be precise and cite sources when possible."),
                new ChatMessage(ChatMessageRole.USER.value(),
                    "Context:\n" + context + "\n\nQuestion: " + question)
            );

            ChatCompletionRequest chatRequest = ChatCompletionRequest.builder()
                .model("gpt-4")
                .messages(messages)
                .temperature(0.3)
                .build();

            return openAiService.createChatCompletion(chatRequest)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
