/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.dto.aiml;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author javaugi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAIRequest {
    private String model;          // e.g., "deepseek-chat"
    private List<Message> messages;
    private double temperature;    // (Optional) Default: 0.7
    private int max_tokens;        // (Optional) Default: 512

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;        // "user" or "assistant"
        private String content;
    }    
}
