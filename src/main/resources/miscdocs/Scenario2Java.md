import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class RestTemplateConfig {

    // Path to your truststore file (e.g., truststore.jks or truststore.p12)
    @Value("classpath:truststore.jks") // Or file:/path/to/truststore.jks
    private Resource trustStore;

    // Password for your truststore
    @Value("${truststore.password}")
    private String trustStorePassword;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) throws Exception {
        // Load the truststore
        KeyStore trustMaterial = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream is = trustStore.getInputStream()) {
            trustMaterial.load(is, trustStorePassword.toCharArray());
        }

        // Create an SSLContext that trusts certificates in your truststore
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(trustMaterial, null) // Load trust material from your truststore
                .build();

        // Create an SSLConnectionSocketFactory with the custom SSLContext
        // Use default hostname verifier for production (StrictHostnameVerifier)
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);

        // Build the CloseableHttpClient with the custom SSLConnectionSocketFactory
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        // Create an HttpComponentsClientHttpRequestFactory with the custom HttpClient
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        // Build the RestTemplate using the builder and the custom request factory
        return builder
                .requestFactory(() -> requestFactory)
                .build();
    }
}
