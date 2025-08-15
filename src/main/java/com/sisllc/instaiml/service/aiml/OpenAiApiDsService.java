/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.service.aiml;

import com.sisllc.instaiml.config.AiConfig;
import static com.sisllc.instaiml.config.AiConfig.OLLAMA_API;
import com.sisllc.instaiml.data.aiml.OpenAIRequest;
import com.sisllc.instaiml.data.aiml.OpenAIResponse;
import com.sisllc.instaiml.dto.OllamaRequest;
import io.netty.handler.timeout.ReadTimeoutException;
import java.net.ConnectException;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class OpenAiApiDsService {

    public static final String ML_Q = "what are the output formats from unstructured document processed by AI ML";

    @Value("${spring.ai.deepseek.openai.base-url}")
    private String dsBaseUrl;
    @Value("${spring.ai.deepseek.openai.api-key}")
    private String dsApiKey;
    @Value("${spring.ai.deepseek.openai.chat.options.model}")
    private String dsModelDefault;

    @Qualifier(AiConfig.REST_TEMPLATE)
    private final RestTemplate restTemplate;

    //(Use this if the API returns a stream like Server-Sent Events)
    public Mono<String> queryOpenAiDsByWebClient(OllamaRequest ollamaRequest) {
        //String requestBody = "{\"model\":\"deepseek-llm\", \"prompt\":\"" + ollamaRequest.prompt() + "\"}";
        HttpHeaders headers = setupHeaders();
        return WebClient
            .builder()
            .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30))))
            .build()
            .post()
            .uri(dsBaseUrl)
            .headers(h -> h.addAll(headers)) // This is the Consumer<HttpHeaders>
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(ollamaRequest)
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(), response -> {
                // 4. Log error response details
                log.error("queryOllamaByWebClient Request failed with status: {}", response.statusCode());
            return response.bodyToMono(String.class)                    .defaultIfEmpty("queryOllamaByWebClient No error empty body")
                    .flatMap(errorBody -> {
                        log.error("queryOllamaByWebClient Error response body: {}", errorBody);
                    return Mono.error(new RuntimeException(                            "HTTP " + response.statusCode() + " - " + errorBody
                        ));
                    });
            })
            .bodyToMono(String.class)
            // 5. Add response stream logging
            .doOnNext(chunk -> log.debug("Received chunk: {}", chunk))
            .doOnSubscribe(sub -> log.debug("Starting request"))
            //.doOnComplete(() -> log.debug("Stream completed successfully"))
            .doOnError(e -> {
            if (e instanceof ConnectException) {
                log.error("Failed to connect to Ollama service at {}", OLLAMA_API);
            } else if (e instanceof ReadTimeoutException) {
                    log.error("Timeout while waiting for Ollama response");
                }
                log.error("Query error occurred", e);
            })
            .doOnCancel(() -> log.warn("Query was cancelled"));
    }

    //(Use this if the API returns a stream like Server-Sent Events)
    public Flux<String> streamOpenAiDsByWebClient(OllamaRequest ollamaRequest) {
        //String requestBody = "{\"model\":\"deepseek-llm\", \"prompt\":\"" + ollamaRequest.prompt() + "\"}";
        HttpHeaders headers = setupHeaders();
        return WebClient
            .builder()
            .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30))))
            .build()
            .post()
            .uri(dsBaseUrl)
            .headers(h -> h.addAll(headers)) // This is the Consumer<HttpHeaders>
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(ollamaRequest)
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(), response -> {
                // 4. Log error response details
                log.error("queryOllamaByWebClient Request failed with status: {}", response.statusCode());
                return response.bodyToMono(String.class).defaultIfEmpty("queryOllamaByWebClient No error empty body")
                    .flatMap(errorBody -> {
                        log.error("queryOllamaByWebClient Error response body: {}", errorBody);
                        return Mono.error(new RuntimeException("HTTP " + response.statusCode() + " - " + errorBody
                        ));
                    });
            })
            .bodyToFlux(String.class)
            // 5. Add response stream logging
            .doOnNext(chunk -> log.debug("Received chunk: {}", chunk))
            .doOnSubscribe(sub -> log.debug("Starting request"))
            .doOnComplete(() -> log.debug("Stream completed successfully"))
            .doOnError(e -> {
                if (e instanceof ConnectException) {
                    log.error("Failed to connect to Ollama service at {}", OLLAMA_API);
                } else if (e instanceof ReadTimeoutException) {
                    log.error("Timeout while waiting for Ollama response");
                }
                log.error("Query error occurred", e);
            })
            .doOnCancel(() -> log.warn("Query was cancelled"));
    }

    public Mono<String> getChatResponse(String userPrompt) {
        log.debug("getChatResponse userPrompt {}", userPrompt);
        try {
            //if (userPrompt == null || userPrompt.isEmpty()) {
            //    userPrompt = ML_Q;
            //}

            // 1. Prepare Headers
            HttpHeaders headers = setupHeaders();

            // 2. Build Request Body
            OpenAIRequest request = new OpenAIRequest(dsModelDefault, // Model name
                List.of(new OpenAIRequest.Message("user", userPrompt)), // Messages
                0.7, /* Temperature */ 512 /* Max tokens */
            );
            log.debug("getChatResponse done request {}", request);

            // 3. Send HTTP Request
            HttpEntity<OpenAIRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<OpenAIResponse> response = restTemplate.exchange(dsBaseUrl, HttpMethod.POST, entity, OpenAIResponse.class);
            log.debug("getChatResponse done response {}", response);
            if (response == null || response.getBody() == null) {
                return Mono.empty();
            }

            // 4. Extract AI Response
            OpenAIResponse aiResponse = response.getBody();
            if (aiResponse == null || aiResponse.getChoices() == null || aiResponse.getChoices().isEmpty()) {
                return Mono.empty();
            }

            return Mono.justOrEmpty(aiResponse.getChoices().get(0).getMessage().getContent());
        } catch (RestClientException ex) {
            log.error("Error getChatResponse", ex);
        }
        return Mono.empty();
    }

    public Mono<String> getAIResponse(String prompt) {
        HttpHeaders headers = setupHeaders();
        String requestBody = "{\"model\":\"deepseek-chat\",\"messages\":[{\"role\":\"user\",\"content\":\"" + prompt + "\"}]}";

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(dsBaseUrl, request, String.class);

        return Mono.justOrEmpty(response.getBody());
    }

    private HttpHeaders setupHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", dsApiKey); // If API key is needed
        headers.setBearerAuth(dsApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
