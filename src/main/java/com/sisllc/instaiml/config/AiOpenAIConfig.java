/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.config;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 *
 * @author javaugi
 */
@Configuration
public class AiOpenAIConfig implements CommandLineRunner{
    private static final Logger log = LoggerFactory.getLogger(AiOpenAIConfig.class);
    
    @Value("classpath:/prompts/system-message.st")
    private Resource systemResource;    
    @Value("${spring.ai.openai.uri}")
    private String baseUrl;
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    @Value("${openai.api.model}")
    private String apiModel;
    
    @Override
    public void run(String... args) throws Exception {
        log.debug("AiDeepSeekConfig with baseUrl {} args {} ", baseUrl, Arrays.toString(args));
        hide(systemResource, apiKey, apiModel);
    }

    private void hide(Resource resource, String... args) {
        //doNothing
    }   
    
    @Bean(name = AiConfig.OPEN_AI_API)
    public OpenAiApi openAiApi() {
        return OpenAiApi.builder().baseUrl(baseUrl).apiKey(apiKey).build();
    }

    @Bean(name = AiConfig.OPEN_AI_CHAT_MODEL)
    public OpenAiChatModel openAiChatModel(@Qualifier(AiConfig.OPEN_AI_API) OpenAiApi openAiApi) {
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder().model(apiModel).maxTokens(4096).temperature(0.7).build())
                .build();
    }      
       
}
