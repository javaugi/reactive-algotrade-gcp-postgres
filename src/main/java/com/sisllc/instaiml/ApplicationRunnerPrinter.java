/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml;

import static com.sisllc.instaiml.config.AiConfig.OLLAMA_API;
import com.sisllc.instaiml.config.DatabaseProperties;
import java.util.Arrays;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ApplicationRunnerPrinter implements ApplicationRunner {

    @Autowired 
    private Environment env;
    @Autowired
    protected DatabaseProperties dbProps;

    @Override
    public void run(ApplicationArguments args) {
        log.debug("ApplicationRuunerPrinter profiles {} java.home={}", Arrays.toString(env.getActiveProfiles()), System.getProperty("java.home"));

        Set<String> names = args.getOptionNames();
        log.debug("Raw args: " + names);
        for (String name : names) {
            log.debug("Option name={}, value={}", name, args.getOptionValues(name));
        }
        /*
        java -jar target/compass.jar --spring.profiles.active=mock --debug=true
            Raw args: [debug, spring.profiles.active]
            Option name=debug, value=[true]
            Option name=spring.profiles.active, value=[mock]
         */

        // This method provides a more comprehensive dump including different property sources
        // Be cautious, this can print a lot of sensitive information in production.
        ((AbstractEnvironment) env).getPropertySources()
            .forEach(ps -> {
                if (ps instanceof MapPropertySource mapPropertySource) {
                    mapPropertySource.getSource().keySet()
                        .stream()
                        .filter(k -> (k.startsWith("spring.r2dbc") || k.startsWith("spring.datasource")))
                        .forEach(k -> {
                                log.debug("ENV key={}  value={}", k, mapPropertySource.getSource().get(k));
                            }
                        );
                } else {
                    //log.info("Non MapPropertySource: " + ps.getName() + " (not a MapPropertySource, cannot iterate directly)");
                    // For non-MapPropertySource types (like system properties, command line args),
                    // iterating all keys is not directly supported by the PropertySource interface itself.
                    // You'd typically access specific keys via env.getProperty()
                }
            });

        log.info("Application is ready: \n {}", dbProps);
        healthCheck()
            .doOnError(e -> log.info("HealthCheck Error", e))
            .doOnNext(e -> log.info("HealthCheck Success", e))
            .subscribe();
        log.info("ApplicationRunnerPrinter Done");
    }

    public Mono<ResponseEntity<String>> healthCheck() {
        return WebClient.create(OLLAMA_API)
            .get()
            .retrieve()
            .toBodilessEntity()
            .map(response -> ResponseEntity.ok("Ollama connection OK"))
            .onErrorResume(e -> Mono.just(
            ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Ollama connection failed: " + e.getMessage())
        ));
    }
       
}

/*
In Spring Boot, both CommandLineRunner and ApplicationRunner are interfaces used to execute code after the application context is loaded but 
    before the application starts accepting requests. They are useful for tasks like database initialization, setting up default data,
    or running startup logic.

Key Similarities:
    Both are executed after the ApplicationContext is created but before SpringApplication.run() completes.
    Both can have multiple implementations, and you can control their order using @Order or by implementing Ordered.
    Both are part of Spring Boot's startup lifecycle.

Differences Between CommandLineRunner and ApplicationRunner:
    Feature                 CommandLineRunner                   ApplicationRunner
    Method to Implement     run(String... args)                 run(ApplicationArguments args)
    Argument Handling       Raw command-line args (String[])	Parsed arguments (ApplicationArguments)
    Use Case                Simple CLI argument handling        Advanced argument parsing (e.g., --key=value)
    Example                 java -jar app.jar arg1 arg2         java -jar app.jar --name=John --debug

Example: CommandLineRunner
    java
    import org.springframework.boot.CommandLineRunner;
    import org.springframework.core.annotation.Order;
    import org.springframework.stereotype.Component;

    @Component
    @Order(1) // Optional ordering
    public class MyCommandLineRunner implements CommandLineRunner {
        @Override
        public void run(String... args) {
            System.out.println("CommandLineRunner executed with args: " + Arrays.toString(args));
        }
    }
    Runs with raw String[] arguments.
    Example input: java -jar app.jar hello world → args = ["hello", "world"]

    Example: ApplicationRunner
    java
    import org.springframework.boot.ApplicationArguments;
    import org.springframework.boot.ApplicationRunner;
    import org.springframework.stereotype.Component;

    @Component
    public class MyApplicationRunner implements ApplicationRunner {
        @Override
        public void run(ApplicationArguments args) {
            System.out.println("ApplicationRunner executed");
            System.out.println("Raw args: " + Arrays.toString(args.getSourceArgs()));
            System.out.println("Option names: " + args.getOptionNames());
            System.out.println("--name value: " + args.getOptionValues("name"));
        }
    }
    Provides structured argument parsing.

    Example input: java -jar app.jar --name=John --debug
    → args.getOptionValues("name") = ["John"]
    → args.getOptionValues("debug") = [] (flag)

When to Use Which?
    Use CommandLineRunner if you need simple access to raw command-line arguments.
    Use ApplicationRunner if you want to work with parsed arguments (e.g., --key=value pairs).
    Both can coexist in the same application, and you can control their execution order using @Order
*/
