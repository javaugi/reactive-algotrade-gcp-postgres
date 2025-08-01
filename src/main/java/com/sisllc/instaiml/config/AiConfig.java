/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.net.ssl.SSLContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@Configuration
public class AiConfig implements CommandLineRunner {

    public static final String REST_TEMPLATE = "restTemplate";
    public static final String SECURE_REST_TEMPLATE = "secureRestTemplate";
    public static final String OPEN_AI_API = "openAiApi";
    public static final String OPEN_AI_CHAT_MODEL = "openAiChatModel";
    public static final String OPEN_AI_API_DS = "openAiApiDeepsk";
    public static final String OPEN_AI_CHAT_MODEL_DS = "openAiChatModelDeepsk";
    //Option 1: Run DeepSeek Locally with Ollama (Recommended for Free)
    public static final String OLLAMA_API = "http://localhost:11434/api/generate";

    @Override
    public void run(String... args) throws Exception {
        log.debug("AiConfig with args {}", Arrays.toString(args));
    }

    @Primary
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector())
            .build();
    }

    @Bean
    public RestTemplateBuilder builder() {
        return new RestTemplateBuilder();
    }

    @Primary
    @Bean(name = REST_TEMPLATE)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(name = SECURE_REST_TEMPLATE)
    public RestTemplate secureRestTemplate(RestTemplateBuilder builder) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        // Create a custom SSLContext that trusts all certificates
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, (chain, authType) -> true) // Trust all certificates
                .build();

        // Create an SSLConnectionSocketFactory with the custom SSLContext and NoopHostnameVerifier
        // NoopHostnameVerifier does not verify hostnames, which is insecure for production
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        // Build the CloseableHttpClient with the custom SSLConnectionSocketFactory
        CloseableHttpClient httpClient = HttpClients.custom().build();
                //.setSSLSocketFactory(sslSocketFactory)
                //.build();

        // Create an HttpComponentsClientHttpRequestFactory with the custom HttpClient
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        // Build the RestTemplate using the builder and the custom request factory
        return builder.requestFactory(() -> requestFactory)
                .build();
    }    
    
}
