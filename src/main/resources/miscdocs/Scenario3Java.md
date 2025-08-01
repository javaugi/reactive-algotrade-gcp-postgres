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

    // Client keystore (contains your client certificate and private key)
    @Value("classpath:client.p12") // Or client.jks
    private Resource clientKeyStore;
    @Value("${client.keystore.password}")
    private String clientKeyStorePassword;
    @Value("${client.key.password}") // Often same as keystore password
    private String clientKeyPassword;

    // Truststore (contains server's trusted certificates)
    @Value("classpath:truststore.jks")
    private Resource trustStore;
    @Value("${truststore.password}")
    private String trustStorePassword;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) throws Exception {
        // Load client keystore
        KeyStore clientKs = KeyStore.getInstance("PKCS12"); // Or "JKS"
        try (InputStream is = clientKeyStore.getInputStream()) {
            clientKs.load(is, clientKeyStorePassword.toCharArray());
        }

        // Load truststore
        KeyStore trustMaterial = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream is = trustStore.getInputStream()) {
            trustMaterial.load(is, trustStorePassword.toCharArray());
        }

        // Create SSLContext with client key material and trusted server certificates
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(clientKs, clientKeyPassword.toCharArray()) // Your client certificate
                .loadTrustMaterial(trustMaterial, null) // Trusted server certificates
                .build();

        // Create an SSLConnectionSocketFactory with the custom SSLContext
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

