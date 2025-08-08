/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

// @SpringBootTest loads the entire application context.
// webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT starts a real server on a random port.
// @ActiveProfiles("mock") will apply the configurations from application-mock.properties,
// disabling the problematic Cloud SQL R2DBC auto-configuration.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mock")
public class HealthCheckTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnHealthStatusUp() {
        // Test the Spring Boot Actuator health endpoint
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("\"status\":\"UP\""), "Health status should be UP.");
    }
}

/*
Common Actuator Endpoints
    Endpoint                Description
    /actuator/health        Shows health info
    /actuator/info          Shows info from application.properties
    /actuator/metrics       Metrics like JVM, CPU, DB, HTTP reqs
    /actuator/env           Shows environment variables and props
    /actuator/beans         Shows all Spring beans
    /actuator/loggers       Change log levels at runtime
    /actuator/prometheus    App health details and system gauge from Prometheus

Summary
    @SpringBootApplication + Actuator exposes useful diagnostics /actuator/health gives system health (DB, disk, custom indicators)
    Extend with custom health indicators
    Integrates with Prometheus, Grafana, Azure Monitor, etc.
 */
