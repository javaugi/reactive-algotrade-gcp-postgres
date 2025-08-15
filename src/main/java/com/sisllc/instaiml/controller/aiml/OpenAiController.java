/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.controller.aiml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sisllc.instaiml.service.aiml.OpenAiApiService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/openai")
public class OpenAiController {

    private final static Logger log = LoggerFactory.getLogger(OpenAiController.class);

    private final OpenAiApiService openAIService;

    @GetMapping
    public Flux<String> getOpenAIResponse(@RequestParam String prompt) {
        return openAIService.queryOpenAI(prompt);
    }

    @GetMapping("/getdata")
    public Flux<String> rootEndpoint() {
        try {
            return openAIService.getData();
        } catch (JsonProcessingException e) {
            log.error("Error while processing JSON", e);
            return Flux.empty();
        }
    }
}
/*

@RestController
public class OpenAiController {

    private final OpenAiApiService openAIService;

    public OpenAiController(OpenAiApiService openAIService) {
        this.openAIService = openAIService;
    }
    
    //curl "http://localhost:8080/chat?prompt=Tell me a joke"
    @GetMapping("/chat")
    public String chat(@RequestParam String prompt) {
        return openAIService.getResponse(prompt);
    }
}
// */
