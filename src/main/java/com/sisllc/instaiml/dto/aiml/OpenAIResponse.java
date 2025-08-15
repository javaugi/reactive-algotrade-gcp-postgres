/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.dto.aiml;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class OpenAIResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices = new ArrayList<>();

    @Data
    public static class Choice {
        private Message message;
        private int index;
        private String finish_reason;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }    
}
