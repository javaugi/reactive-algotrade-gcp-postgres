/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.dto.aiml;

public record OllamaRequest(String model, String prompt) {
    public OllamaRequest(String model) {
        this("deepseek-llm", "");
    }

    public OllamaRequest(String model, String prompt) {
        this.model = "deepseek-llm";
        this.prompt = "Explain quantum computing";
    }
}
