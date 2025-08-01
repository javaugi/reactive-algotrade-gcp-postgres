When you talk about a "secure" RestTemplate, you're generally referring to its ability to handle HTTPS connections securely (trusting certificates, 
    potentially using client certificates) and sometimes to integrate with authentication mechanisms like Basic Auth.

You're correct to bring up RestTemplateBuilder and Apache HttpClient. RestTemplateBuilder is the recommended way to create RestTemplate instances 
    in Spring Boot, as it provides auto-configuration and allows for easy customization. Apache HttpClient is a robust underlying HTTP client that 
    offers more advanced configuration options for SSL/TLS than the default Java HttpURLConnection.

Here's how you can create a secure RestTemplate using RestTemplateBuilder and Apache HttpClient 4.x.


1. Dependencies
Make sure you have the following dependencies in your pom.xml:
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version> <!-- Use a recent Spring Boot version -->
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.example</groupId>
    <artifactId>secure-rest-template</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>secure-rest-template</name>
    <description>Demo project for secure RestTemplate</description>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.5.14</version> <!-- Your specified version -->
        </dependency>
        <!-- For SSL/TLS configuration utilities -->
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpcore</artifactId>
            <version>4.4.16</version> <!-- Compatible with httpclient 4.5.14 -->
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>

Explanation:
    spring-boot-starter-web: Provides RestTemplateBuilder and necessary web infrastructure.
    httpclient: The Apache HttpClient library itself.
    httpcore: A core dependency for httpclient.

2. Configuring RestTemplateBuilder for HTTPS (SSL/TLS)
The key to configuring SSL/TLS with RestTemplateBuilder is to provide a custom ClientHttpRequestFactory that uses Apache HttpClient. This factory allows 
    you to inject SSLContext and HostnameVerifier configurations.
Here are a few common scenarios for SSL/TLS configuration:

Scenario 1: Trust All Certificates (Development/Testing ONLY - INSECURE FOR PRODUCTION)
    This is the simplest way to get HTTPS working if you're hitting an endpoint with a self-signed or untrusted certificate during development. Never use 
        this in production.
Scenario 2: Trust Specific Certificates (Recommended for Production)
    This is the secure way to configure SSL/TLS. You provide a Java KeyStore (JKS) or PKCS12 file containing the trusted server certificates (or the CA 
        certificates that signed them).

    To create a truststore.jks:
        You can import server certificates into a JKS file using Java's keytool utility:
            keytool -import -trustcacerts -alias my-server-cert -file server.crt -keystore truststore.jks -storepass changeit
    application.properties for Scenario 2:
        Properties
        truststore.jks = file:/path/to/truststore.jks
        truststore.password=your_truststore_password
Scenario 3: Client Certificates (Mutual TLS/mTLS)
    For highly secure applications, you might need to provide a client certificate to the server (Mutual TLS). This requires a KeyStore (for your client 
        certificate) and a TrustStore (for the server's certificate).
    application.properties for Scenario 3:
        Properties
            client.keystore.password=your_client_keystore_password
            client.key.password=your_client_key_password
            truststore.password=your_truststore_password
3. Adding Basic Authentication
    You can add basic authentication directly using RestTemplateBuilder or by adding an interceptor.

    Option A: Using RestTemplateBuilder.basicAuthentication()
        This is the simplest way for basic authentication.

        @Configuration
        public class RestTemplateConfig {

            @Bean
            public RestTemplate restTemplate(RestTemplateBuilder builder) {
                return builder
                        .basicAuthentication("username", "password") // Add basic authentication
                        .build();
            }
        }

    Option B: Using a ClientHttpRequestInterceptor (More Flexible)
        For more complex authentication (e.g., adding a bearer token, dynamic credentials), an interceptor is more flexible.

            @Configuration
            public class RestTemplateConfig {

                @Bean
                public RestTemplate restTemplate(RestTemplateBuilder builder) {
                    return builder
                            .interceptors(new BasicAuthInterceptor("myuser", "mypass")) // Add your custom interceptor
                            .build();
                }

                // Custom Interceptor for Basic Authentication
                private static class BasicAuthInterceptor implements ClientHttpRequestInterceptor {
                    private final String username;
                    private final String password;

                    public BasicAuthInterceptor(String username, String password) {
                        this.username = username;
                        this.password = password;
                    }

                    @Override
                    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                        String auth = username + ":" + password;
                        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
                        String authHeader = "Basic " + new String(encodedAuth);
                        request.getHeaders().add(HttpHeaders.AUTHORIZATION, authHeader);
                        return execution.execute(request, body);
                    }
                }
            }

Combining SSL/TLS and Authentication
    You can combine these approaches by chaining the RestTemplateBuilder methods:
    This comprehensive guide should help you create and configure a secure RestTemplate for your Spring Boot applications, covering various SSL/TLS 
        and authentication scenarios. Remember to always prioritize security best practices, especially in production environments.

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Base64;

@Configuration
public class RestTemplateConfig {

    // Client keystore (contains your client certificate and private key)
    @Value("classpath:client.p12")
    private Resource clientKeyStore;
    @Value("${client.keystore.password}")
    private String clientKeyStorePassword;
    @Value("${client.key.password}")
    private String clientKeyPassword;

    // Truststore (contains server's trusted certificates)
    @Value("classpath:truststore.jks")
    private Resource trustStore;
    @Value("${truststore.password}")
    private String trustStorePassword;

    @Bean
    public RestTemplate secureRestTemplate(RestTemplateBuilder builder) throws Exception {
        // --- SSL/TLS Configuration (from Scenario 3) ---
        KeyStore clientKs = KeyStore.getInstance("PKCS12");
        try (InputStream is = clientKeyStore.getInputStream()) {
            clientKs.load(is, clientKeyStorePassword.toCharArray());
        }

        KeyStore trustMaterial = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream is = trustStore.getInputStream()) {
            trustMaterial.load(is, trustStorePassword.toCharArray());
        }

        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(clientKs, clientKeyPassword.toCharArray())
                .loadTrustMaterial(trustMaterial, null)
                .build();

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        // --- End SSL/TLS Configuration ---

        return builder
                .requestFactory(() -> requestFactory) // Apply the custom HTTP client for SSL
                .basicAuthentication("api_user", "api_password") // Apply basic authentication
                // Or use .interceptors(new BasicAuthInterceptor(...)) for more control
                .build();
    }

    // If you prefer the interceptor approach for basic auth:
    // private static class BasicAuthInterceptor implements ClientHttpRequestInterceptor {
    //     private final String username;
    //     private final String password;
    //
    //     public BasicAuthInterceptor(String username, String password) {
    //         this.username = username;
    //         this.password = password;
    //     }
    //
    //     @Override
    //     public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    //         String auth = username + ":" + password;
    //         byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
    //         String authHeader = "Basic " + new String(encodedAuth);
    //         request.getHeaders().add(HttpHeaders.AUTHORIZATION, authHeader);
    //         return execution.execute(request, body);
    //     }
    // }
}