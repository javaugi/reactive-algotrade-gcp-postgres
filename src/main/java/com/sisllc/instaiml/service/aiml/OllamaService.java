/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.service.aiml;

import com.sisllc.instaiml.config.AiConfig;
import static com.sisllc.instaiml.config.AiConfig.OLLAMA_API;
import com.sisllc.instaiml.dto.OllamaRequest;
import io.netty.handler.timeout.ReadTimeoutException;
import java.net.ConnectException;
import java.time.Duration;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

/*
Step 2: Pull the DeepSeek Model
Ollama supports DeepSeek models (like deepseek-coder for code or deepseek-llm for general tasks).
Available DeepSeek Models on Ollama:
    deepseek/deepseek-llm:7b (General-purpose LLM)
    deepseek/deepseek-coder:6.7b (Code-focused LLM)
*/
@Slf4j
@RequiredArgsConstructor
@Service
public class OllamaService {

    @Qualifier(AiConfig.REST_TEMPLATE)
    private final RestTemplate restTemplate;

    //public static final String OLLAMA_API = "http://localhost:11434/api/generate";
    /*
curl http://localhost:11434/api/generate -d '{
  "model": "deepseek-llm",
  "prompt": "Explain quantum computing"
}'
     */
    // Call a locally running DeepSeek model (if supported)
    public Mono<String> queryOllamaByWebClient(OllamaRequest ollamaRequest) {
        //String requestBody = "{\"model\":\"deepseek-llm\", \"prompt\":\"" + ollamaRequest.prompt() + "\"}";
        log.debug("queryOllamaByWebClient ollamaRequest {}", ollamaRequest);
        return WebClient
            .builder()
            .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30))))
            .build()
            .post()
            .uri(OLLAMA_API)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(ollamaRequest)
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(), response -> {
                // 4. Log error response details
                log.error("queryOllamaByWebClient Request failed with status: {}", response.statusCode());
                return response.bodyToMono(String.class)
                    .defaultIfEmpty("queryOllamaByWebClient No error empty body")
                    .flatMap(errorBody -> {
                    log.error("queryOllamaByWebClient Error response body: {}", errorBody);
                        return Mono.error(new RuntimeException(
                            "HTTP " + response.statusCode() + " - " + errorBody
                        ));
                    });
            })
            .bodyToMono(String.class)
            .onErrorResume(WebClientResponseException.class, ex -> {
                log.debug("queryByWebClient WebClientResponseException ", ex);
                // Provide a more specific error message for a 403 Forbidden error
                if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                    return Mono.error(new RuntimeException("403 FORBIDDEN: Your API key is likely invalid or missing permissions."));
                }
                // For other WebClient errors, re-throw a more generic exception
                return Mono.error(new RuntimeException("Error from API: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString(), ex));
            });
    }

    //(Use this if the API returns a stream like Server-Sent Events)
    public Flux<String> streamOllamaByWebClient(OllamaRequest ollamaRequest) {
        //String requestBody = "{\"model\":\"deepseek-llm\", \"prompt\":\"" + prompt + "\"}";
        Consumer<HttpHeaders> headersConsumer = existingHeaders -> {
            existingHeaders.setContentType(MediaType.APPLICATION_JSON);
            existingHeaders.set("X-Custom-Header", "value");
            // Add more header modifications as needed
        };

        log.debug("streamOllamaByWebClient ollamaRequest {}", ollamaRequest);
        // 3. Create WebClient with ollamaRequest logging
        return WebClient.create()
            .post()
            .uri(OLLAMA_API)
            .headers(headersConsumer)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(ollamaRequest)
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(), response -> {
                // 4. Log error response details
                log.error("Request failed with status: {}", response.statusCode());
                return response.bodyToMono(String.class)
                    .defaultIfEmpty("No error empty body")
                    .flatMap(errorBody -> {
                        log.error("Error response body: {}", errorBody);
                        return Mono.error(new RuntimeException(
                            "HTTP " + response.statusCode() + " - " + errorBody
                        ));
                    });
            })
            .bodyToFlux(String.class)
            // 5. Add response stream logging
            .doOnNext(chunk -> log.debug("Received chunk: {}", chunk))
            .doOnSubscribe(sub -> log.debug("Starting stream request"))
            .doOnComplete(() -> log.debug("Stream completed successfully"))
            .doOnError(e -> {
                if (e instanceof ConnectException) {
                    log.error("Failed to connect to Ollama service at {}", OLLAMA_API);
                } else if (e instanceof ReadTimeoutException) {
                    log.error("Timeout while waiting for Ollama response");
                }
                log.error("Query error occurred", e);
            })
            .doOnCancel(() -> log.warn("Stream was cancelled"));
    }

    public Mono<String> queryOllamaByTemplate(OllamaRequest ollamaRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = """
            {
                "model": "deepseek-llm",
                "prompt": "%s",
                "stream": false
            }
            """.formatted(ollamaRequest.prompt());

        log.debug("queryOllamaByTemplate requestBody {}", ollamaRequest);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(OLLAMA_API, request, String.class);
        log.debug("queryOllamaByTemplate response.getBody() {}", response.getBody());

        return Mono.justOrEmpty(response.getBody())
            .doOnNext(chunk -> log.debug("Received chunk: {}", chunk))
            .doOnSubscribe(sub -> log.debug("Starting stream request"))
            .doOnError(e -> {
                if (e instanceof ConnectException) {
                    log.error("Failed to connect to Ollama service at {}", OLLAMA_API);
                } else if (e instanceof ReadTimeoutException) {
                    log.error("Timeout while waiting for Ollama response");
                }
                log.error("Query error occurred", e);
            })
            .doOnCancel(() -> log.warn("Stream was cancelled"));
    }

}

/*
Key differences of WebClient vs RestTemplate:

Using WebClient instead of RestTemplate (reactive vs blocking)
    Properly setting content type
    Returning a reactive type (Mono/Flux)
    The actual network call happens when the Mono/Flux is subscribed to, not when the method is called

Note: If you're using Spring WebFlux, you might want to:
    Autowire a WebClient bean instead of creating it each time
    Consider using a proper DTO class instead of raw String for request/response
    Add error handling (e.g., .onErrorResume())

Also, be careful with your string concatenation for JSON - consider using a proper JSON library to avoid injection issues or malformed JSON.
 */

/*
Option 1: Run DeepSeek Locally with Ollama (Recommended for Free)
Ollama lets you run open-source LLMs (like DeepSeek, Llama 3, Mistral) locally. We’ll call it from Spring Boot.

Step 1: Install Ollama
Download & Install

Ollama Official Site (macOS/Linux/WSL)

For Windows (WSL):

bash
wget https://ollama.ai/download/OllamaSetup.exe
Pull DeepSeek Model (or any open LLM)

bash
ollama pull deepseek-llm  # Replace with available model if needed
(If DeepSeek isn’t available, try ollama pull llama3 or mistral.)

Run the Model

bash
ollama run deepseek-llm
(Test with a prompt to confirm it works.)

Step 2: Spring Boot Setup
Create a Spring Boot Project

Use Spring Initializr with:

Spring Web

Lombok (optional)

Call Ollama API from Spring Boot
Ollama runs a local REST API at http://localhost:11434.

Service Class (OllamaService.java)

java
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OllamaService {
    
    private final String OLLAMA_API = "http://localhost:11434/api/generate";
    private final RestTemplate restTemplate = new RestTemplate();

    public String queryOllamaByTemplate(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = """
            {
                "model": "deepseek-llm",
                "prompt": "%s",
                "stream": false
            }
            """.formatted(prompt);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
            OLLAMA_API, 
            request, 
            String.class
        );

        return response.getBody();
    }
}
Create a REST Controller (AiController.java)

java
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final OllamaService ollamaService;

    public AiController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/ask")
    public String ask(@RequestBody String prompt) {
        return ollamaService.queryOllamaByTemplate(prompt);
    }
}
Test It!
Run Spring Boot (mvn spring-boot:run) and send a POST request:

bash
curl -X POST http://localhost:8080/api/ai/ask -d "What is Spring Boot?"
You’ll get a response from your local AI model!

Option 2: Use Transformers (Python + Spring Boot)
If you prefer Python-based models (like Hugging Face’s transformers), you can run a Python AI server and call it from Spring Boot.

Step 1: Set Up Python AI Server
Install Python dependencies:

bash
pip install flask transformers torch
Create ai_server.py:

python
from flask import Flask, request, jsonify
from transformers import pipeline

app = Flask(__name__)
model = pipeline("text-generation", model="deepseek-ai/deepseek-llm-7b")  # Example model

@app.route("/ask", methods=["POST"])
def ask():
    prompt = request.json.get("prompt")
    response = model(prompt, max_length=50)
    return jsonify({"response": response[0]["generated_text"]})

if __name__ == "__main__":
    app.run(port=5000)
Run the server:

bash
python ai_server.py
Step 2: Call Python AI from Spring Boot
Modify OllamaService to call the Python server instead:

java
public String queryOllamaByTemplate(String prompt) {
    String PYTHON_API = "http://localhost:5000/ask";
    String requestBody = "{\"prompt\":\"" + prompt + "\"}";
    // Rest of the code remains the same (just change URL)
}
Which Option Should You Choose?
Approach	Pros	Cons
Ollama (Local)	Free, Fast, No API limits	Requires local GPU (for big models)
Python Server	Works with Hugging Face models	Slower, Needs Python setup
*/