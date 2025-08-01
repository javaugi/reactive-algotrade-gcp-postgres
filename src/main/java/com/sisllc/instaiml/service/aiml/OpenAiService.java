/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.service.aiml;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.sisllc.instaiml.data.aiml.CompletionRequest;
import com.sisllc.instaiml.data.aiml.EventData;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class OpenAiService {

    private final static Logger log = LoggerFactory.getLogger(OpenAiService.class);

    private final String prompt = """
                Explain quantum computing.
                """;

    @Value("${spring.ai.openai.uri}")
    private String openAiUrl;

    @Value("${spring.ai.openai.api-key}")
    private String openAiKey;

    private WebClient client;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    @PostConstruct
    public void init() {
        client = WebClient.builder()
                .baseUrl(openAiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("api-key", openAiKey)
                .build();
    }

    //(Use this if the API returns a stream like Server-Sent Events)
    public Flux<String> queryOpenAI(String prompt) {
        String requestBody = "{\"model\":\"gpt-3.5-turbo\", \"prompt\":\"" + prompt + "\"}";
        return WebClient.create()
            .post()
            .uri(openAiUrl)
            .headers(h -> {
                h.setContentType(MediaType.APPLICATION_JSON);
                h.set("Authorization", "Bearer " + openAiKey);
                h.set("X-Custom-Header", "custom-value");
            })
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToFlux(String.class);
    }

    public Flux<String> getData() throws JsonProcessingException {
        CompletionRequest request = new CompletionRequest();
        request.setPrompt(prompt);
        request.setMaxTokens(2048);
        request.setTemperature(1.0);
        request.setFrequencyPenalty(0.0);
        request.setPresencePenalty(0.0);
        request.setTopP(0.5);
        request.setBestOf(1);
        request.setStream(true);
        request.setStop(null);

        String requestValue = objectMapper.writeValueAsString(request);
        return client.post()
                .bodyValue(requestValue)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class)
                .mapNotNull(event -> {
                    try {
                        String jsonData = event.substring(event.indexOf("{"), event.lastIndexOf("}") + 1);
                        return objectMapper.readValue(jsonData, EventData.class);
                    } catch (JsonProcessingException | StringIndexOutOfBoundsException e) {
                        return null;
                    }
                })
                .skipUntil(event -> !event.getChoices().get(0).getText().equals("\n"))
                .map(event -> event.getChoices().get(0).getText());
    }

    private HttpHeaders setupHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", openAiKey); // If API key is needed
        headers.setBearerAuth(openAiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
