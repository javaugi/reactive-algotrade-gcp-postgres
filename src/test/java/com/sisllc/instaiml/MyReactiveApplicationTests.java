package com.sisllc.instaiml;

import java.util.Arrays;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

// @WebFluxTest is a slice test for the web layer. It aims to load only relevant web components.
// By activating the 'mock' profile, we ensure our application-mock.properties is used.
@WebFluxTest
@AutoConfigureWebTestClient
@ActiveProfiles("mock")
// Import the auto-configuration that provides WebClient.Builder
@Import(WebClientAutoConfiguration.class)
public class MyReactiveApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    // ApplicationContext to assert that the context loaded successfully
    @Autowired(required = false) // required=false if context might not be fully configured in all slice tests
    private ApplicationContext context;

    //@Test
    public void contextLoads() {
        assertNotNull(context);
        Arrays.stream(context.getBeanDefinitionNames())
            .sorted()
            .forEach(System.out::println);
    }

    //@Test
    void contextLoadsHealth() {
        // Verify that the Spring application context has loaded successfully.
        // If the database auto-configuration was still an issue, this would fail.
        assertNotNull(context, "Application context should not be null.");

        // Optional: Print all loaded bean names for debugging/verification
        System.out.println("Beans loaded in context for MyReactiveApplicationTests:");
        Arrays.stream(context.getBeanDefinitionNames())
            .sorted()
            .forEach(System.out::println);

        // Example: Test a simple endpoint to ensure the web layer is functioning
        webTestClient.get().uri("/api/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class).isEqualTo("Hello, Reactive Spring Boot!");
    }
}
