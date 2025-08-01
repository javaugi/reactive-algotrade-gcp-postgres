/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.aiml;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
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
public class GeminiApiControllerTest {
    // Create a request body as a Map, which will be serialized to JSON
    Map<String, String> REQUEST_BODY = Collections.singletonMap("prompt", "Explain quantum computing");

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testGeminiApiByWebClient() {
        webTestClient.mutate()
            .responseTimeout(Duration.ofSeconds(30)) // Increase timeout
            .build()
            .post()
            .uri(uriBuilder -> uriBuilder.path("/api/gemini")
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
    public void testGeminiApiByTemplate() {
        webTestClient.mutate()
            .responseTimeout(Duration.ofSeconds(30)) // Increase timeout
            .build()
            .post()
            .uri(uriBuilder -> uriBuilder.path("/api/gemini/query")
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
                assertNotNull(response);
                assertTrue(response.contains("quantum"));
            });
    }

}
