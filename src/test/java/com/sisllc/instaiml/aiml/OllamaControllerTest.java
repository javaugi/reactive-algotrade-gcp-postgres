/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.aiml;

import com.sisllc.instaiml.dto.OllamaRequest;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
@Disabled("Temporarily disabled for CICD")
public class OllamaControllerTest {
    // Create a request body as a Map, which will be serialized to JSON
    OllamaRequest REQUEST_BODY = new OllamaRequest("deepseek-llm", "Explain quantum computing");

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void queryOllamaByWebClient() {
        webTestClient.mutate()
            .responseTimeout(Duration.ofSeconds(30)) // Increase timeout
            .build()
            .post()
            .uri(uriBuilder -> uriBuilder.path("/api/ollama")
            .queryParam("prompt", "Explain quantum computing")
            .build()
            )
            .contentType(MediaType.APPLICATION_JSON) // Specify the content type
            .bodyValue(REQUEST_BODY) // Send the JSON body
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .consumeWith(result -> {
                String response = result.getResponseBody();
                System.out.println("Response: " + response);
                // Add your assertions here
                assertNotNull(response);
                assertTrue(response.contains("quantum")); // Simple assertion example
            });
    }

    @Test
    public void testOpenAIStreamEndpoint() {
        webTestClient.mutate()
            .responseTimeout(Duration.ofSeconds(30)) // Increase timeout
            .build()
            .post()
            .uri(uriBuilder -> uriBuilder.path("/api/ollama/stream")
            .queryParam("prompt", "Explain quantum computing")
            .build()
            )
            .contentType(MediaType.APPLICATION_JSON) // Specify the content type
            .bodyValue(REQUEST_BODY) // Send the JSON body
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(String.class)
            .consumeWith(result -> {
                List<String> responses = result.getResponseBody();
                System.out.println("Streamed responses: " + responses);
                // Add your assertions here
                assertNotNull(responses);
                assertFalse(responses.isEmpty());
            });
    }

    @Test
    public void queryOllamaByTemplate() {
        webTestClient.mutate()
            .responseTimeout(Duration.ofSeconds(30)) // Increase timeout
            .build()
            .post()
            .uri(uriBuilder -> uriBuilder.path("/api/ollama/query")
            .queryParam("prompt", "Explain quantum computing")
            .build()
            )
            .contentType(MediaType.APPLICATION_JSON) // Specify the content type
            .bodyValue(REQUEST_BODY) // Send the JSON body
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .consumeWith(result -> {
                String response = result.getResponseBody();
                System.out.println("Response: " + response);
                // Add your assertions here
                assertNotNull(response);
                assertTrue(response.contains("quantum")); // Simple assertion example
            });
    }
}
