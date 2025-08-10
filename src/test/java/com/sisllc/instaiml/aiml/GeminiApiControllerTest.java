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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("mock")
@SpringBootTest
@AutoConfigureWebTestClient
@ExtendWith(MockitoExtension.class)
@Disabled("Temporarily disabled for CICD")
public class GeminiApiControllerTest {

    // Create a request body as a Map, which will be serialized to JSON
    Map<String, String> REQUEST_BODY = Collections.singletonMap("prompt", "Explain quantum computing");

    @Autowired
    private WebTestClient webTestClient;

    /*
    @Mock
    private WebClient.Builder webClientBuilder; // Mock the builder

    @Mock
    private WebClient webClient; // Mock the WebClient built by the builder

    // Mocks for the fluent API chain of WebClient
    @Mock
    private RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private ResponseSpec responseSpec;

    @InjectMocks
    private GeminiApiService geminiApiService; // Inject mocks into your service
    // */

    @BeforeEach
    void setUp() {
        /*
        // Configure the mocked WebClient.Builder to return the mocked WebClient
        when(webClientBuilder.build()).thenReturn(webClient);

        // --- Mock the WebClient fluent API chain ---
        // For a POST request (assuming GeminiApiService makes a POST)
        when(webTestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersUriSpec); // or body(Mono.just(any()), AnyClass.class)
        when(requestHeadersUriSpec.exchange()).thenReturn(responseSpec);
        // */
        //when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("mocked Gemini API response"));
        // Adjust the above chain based on how your GeminiApiService actually uses WebClient
        // e.g., if it uses .get(), .put(), .exchange(), etc.
    }

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
