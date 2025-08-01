/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.service.aiml;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sisllc.instaiml.config.AiConfig;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Service
public class GeminiApiService {

    // Inject the RestTemplate configured for secure communication
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper; // For JSON processing

    // In a real application, the API key would be loaded from properties or Secrets Manager
    // For Canvas environment, it's often handled automatically if left empty in the JS fetch call.
    // However, for Java, you'd typically load it here:
    // @Value("${google.gemini.api-key}")
    @Value("${spring.ai.gemini.base-url}")
    private String geminiApiUrl;
    @Value("${spring.ai.gemini.api-key:}    ")
    private String geminiApiKey; // Leave empty for Canvas auto-injection in JS, or load from properties

    private static final int MAX_RETRIES = 5;
    private static final long INITIAL_BACKOFF_MILLIS = 1000;

    // Constructor to inject WebClient and ObjectMapper
    public GeminiApiService(@Qualifier(AiConfig.REST_TEMPLATE) RestTemplate restTemplate,
        WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Generates text using the Gemini API. Implements exponential backoff for
     * retries.
     *
     * @param prompt The text prompt to send to the Gemini model.
     * @return The generated text response.
     * @throws RuntimeException if the API call fails after retries.
     */
    public Mono<String> queryByWebClient(String prompt) {
        // Build the request body using Jackson ObjectMapper
        ObjectNode rootNode = configRootNode(prompt);
        log.debug("queryByWebClient prompt {} geminiApiUrl {}", prompt, geminiApiUrl);

        // Check if the API key is available
        if (!StringUtils.hasText(geminiApiKey)) {
            return Mono.error(new IllegalArgumentException("Gemini API key is not configured. Please check your application properties."));
        }

        // Use WebClient to make the POST request
        log.debug("queryByWebClient calling webClient geminiApiUrl {}", geminiApiUrl);
        String urlWithApiKey = geminiApiUrl + "?key=" + geminiApiKey;
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30))))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
            .post()
            .uri(urlWithApiKey)
            .body(BodyInserters.fromValue(rootNode))
            .retrieve()
            .bodyToMono(String.class) // Retrieve the response body as a Mono<String>
            .flatMap(responseBody -> {
                try {
                    log.debug("queryByWebClient responseBody {}", responseBody);
                    JsonNode responseJson = objectMapper.readTree(responseBody);
                    JsonNode candidate = responseJson.at("/candidates/0/content/parts/0/text");
                    log.debug("queryByWebClient candidate {}", candidate);
                    if (candidate.isTextual()) {
                        return Mono.just(candidate.asText());
                    } else {
                        // Handle cases where the response structure is unexpected
                        log.warn("Unexpected Gemini API response structure: " + responseBody);
                        return Mono.error(new RuntimeException("Unexpected Gemini API response structure."));
                    }
                } catch (JsonProcessingException e) {
                    return Mono.error(new RuntimeException("Failed to parse Gemini API response.", e));
                }
            })
            .onErrorResume(WebClientResponseException.class, ex -> {
            log.debug("queryByWebClient WebClientResponseException ", ex);
                // Provide a more specific error message for a 403 Forbidden error
                if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                    return Mono.error(new RuntimeException("403 FORBIDDEN: Your API key is likely invalid or missing permissions. Please verify your 'google.gemini.api-key' in application.properties."));
                }
                // For other WebClient errors, re-throw a more generic exception
                return Mono.error(new RuntimeException("Error from Gemini API: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString(), ex));
            });
    }

    /**
     * Generates text using the Gemini API. Implements exponential backoff for
     * retries.
     *
     * @param prompt The text prompt to send to the Gemini model.
     * @return The generated text response.
     * @throws RuntimeException if the API call fails after retries.
     */
    public String queryByTemplate(String prompt) {
        HttpHeaders headers = configHeaders();
        ObjectNode rootNode = configRootNode(prompt);

        // Optional: Add generationConfig for structured responses if needed
        // ObjectNode generationConfig = rootNode.putObject("generationConfig");
        // generationConfig.put("responseMimeType", "application/json");
        // ObjectNode responseSchema = generationConfig.putObject("responseSchema");
        // responseSchema.put("type", "OBJECT");
        // responseSchema.putObject("properties").putObject("generatedText").put("type", "STRING");
        HttpEntity<String> requestEntity = new HttpEntity<>(rootNode.toString(), headers);

        String urlWithApiKey = geminiApiUrl + "?key=" + geminiApiKey;
        for (int i = 0; i < MAX_RETRIES; i++) {
            log.debug("queryByTemplate calling restTemplate try {} prompt {}", i, prompt);
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(urlWithApiKey, requestEntity, String.class);
                log.debug("queryByTemplate response " + response);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonNode responseJson = objectMapper.readTree(response.getBody());
                    JsonNode candidate = responseJson.at("/candidates/0/content/parts/0/text");
                    log.debug("queryByTemplate candidate " + candidate);
                    if (candidate.isTextual()) {
                        return candidate.asText();
                    } else {
                        // Handle cases where the response structure is unexpected
                        log.warn("Unexpected Gemini API response structure: " + response.getBody());
                        throw new RuntimeException("Unexpected Gemini API response structure.");
                    }
                } else {
                    log.warn("Gemini API call failed with status: " + response.getStatusCode() + ", body: " + response.getBody());
                    // Retry for non-success status codes (e.g., 429 Too Many Requests, 5xx errors)
                    if (response.getStatusCode().is4xxClientError() && !response.getStatusCode().equals(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS)) {
                        // Don't retry for client errors unless it's 429
                        throw new RuntimeException("Gemini API client error: " + response.getStatusCode());
                    }
                }
            } catch (JsonProcessingException | RuntimeException e) {
                log.warn("Error calling Gemini API (attempt " + (i + 1) + "): ", e.getMessage());
                if (i == MAX_RETRIES - 1) {
                    throw new RuntimeException("Failed to call Gemini API after " + MAX_RETRIES + " retries.", e);
                }
            }

            // Exponential backoff
            try {
                TimeUnit.MILLISECONDS.sleep(INITIAL_BACKOFF_MILLIS * (long) Math.pow(2, i));
            } catch (InterruptedException ie) {
                log.warn("Gemini API call interrupted during backoff", ie.getMessage());
                Thread.currentThread().interrupt();
                throw new RuntimeException("Gemini API call interrupted during backoff.", ie);
            }
        }
        throw new RuntimeException("Failed to get response from Gemini API after retries.");
    }

    private ObjectNode configRootNode(String prompt) {
        // Build the request body using Jackson ObjectMapper
        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode contentsArray = rootNode.putArray("contents");
        ObjectNode userPart = contentsArray.addObject();
        ArrayNode partsArray = userPart.putArray("parts");
        partsArray.addObject().put("text", prompt);
        return rootNode;
    }

    private HttpHeaders configHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}


/*
curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent" \
  -H 'Content-Type: application/json' \
  -H 'X-goog-api-key: AIzaSyCtr381hUGjhtgDIW82tzR4HhX86bLsAhA' \
  -X POST \
  -d '{
    "contents": [
      {
        "parts": [
          {
            "text": "Explain how AI works in a few words"
          }
        ]
      }
    ]
  }'
 */
