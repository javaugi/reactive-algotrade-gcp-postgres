/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.aiml;

import com.sisllc.instaiml.dto.OllamaRequest;
import com.sisllc.instaiml.service.aiml.GeminiApiService;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

@ActiveProfiles("mock")
@SpringBootTest
@AutoConfigureWebTestClient
@Disabled("Temporarily disabled for CICD")
@ExtendWith(MockitoExtension.class)
public class OllamaControllerTest {
    // Create a request body as a Map, which will be serialized to JSON
    OllamaRequest REQUEST_BODY = new OllamaRequest("deepseek-llm", "Explain quantum computing");

    @Autowired
    private WebTestClient webTestClient;

    @Mock
    private WebClient.Builder webClientBuilder; // Mock the builder

    @Mock
    private WebClient webClient; // Mock the WebClient built by the builder

    // Mocks for the fluent API chain of WebClient
    @Mock
    private WebTestClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebTestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebTestClient.ResponseSpec responseSpec;

    @InjectMocks
    private GeminiApiService geminiApiService; // Inject mocks into your service

    @BeforeEach
    void setUp() {
        // Configure the mocked WebClient.Builder to return the mocked WebClient
        when(webClientBuilder.build()).thenReturn(webClient);

        // --- Mock the WebClient fluent API chain ---
        // For a POST request (assuming GeminiApiService makes a POST)
        when(webTestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersUriSpec); // or body(Mono.just(any()), AnyClass.class)
        //when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        //when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("mocked Gemini API response"));
        // Adjust the above chain based on how your GeminiApiService actually uses WebClient
        // e.g., if it uses .get(), .put(), .exchange(), etc.
    }

    //@Test
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

    //@Test
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

    //@Test
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
