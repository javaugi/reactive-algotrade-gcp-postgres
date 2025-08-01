/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.config;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;

//https://platform.deepseek.com/transactions
//https://platform.deepseek.com/api_keys
@Slf4j
@Configuration
public class AiDsOpenAIConfig implements CommandLineRunner{

    @Value("classpath:/prompts/system-message.st")
    private Resource systemResource;    
    @Value("${spring.ai.deepseek.openai.base-url}")
    private String baseUrl;
    @Value("${spring.ai.deepseek.openai.api-key}")
    private String apiKey;
    @Value("${spring.ai.deepseek.openai.chat.options.model}")
    private String apiModel;
    
    @Override
    public void run(String... args) throws Exception {
        log.debug("AiDsOpenAIConfig with baseUrl {} args {} ", baseUrl, Arrays.toString(args));
        hide(systemResource, apiKey, apiModel);
    }

    private void hide(Resource resource, String... args) {
        //doNothing
    }

    @Primary
    @Bean(name = AiConfig.OPEN_AI_API_DS)
    public OpenAiApi openAiApiDeepsk() {
        return OpenAiApi.builder().baseUrl(baseUrl).apiKey(apiKey).build();
    }

    @Primary
    @Bean(name = AiConfig.OPEN_AI_CHAT_MODEL_DS)
    public OpenAiChatModel openAiChatModelDeepsk(@Qualifier(AiConfig.OPEN_AI_API_DS) OpenAiApi openAiApi) {
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder().model(apiModel).build())
                .build();
    }       
} 