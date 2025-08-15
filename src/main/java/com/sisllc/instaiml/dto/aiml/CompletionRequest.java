/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.dto.aiml;

import java.util.List;
import lombok.Data;

@Data
public class CompletionRequest {

    String prompt;
    Integer maxTokens;
    Double temperature;
    Double frequencyPenalty;
    Double presencePenalty;
    Double topP;
    Integer bestOf;
    Boolean stream;
    List<String> stop;

}