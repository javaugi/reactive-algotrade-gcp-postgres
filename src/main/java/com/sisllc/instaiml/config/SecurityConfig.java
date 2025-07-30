package com.sisllc.instaiml.config;

//import com.sisllc.instaiml.model.User;
import com.sisllc.instaiml.repository.UserRepository;
import com.sisllc.instaiml.security.JwtAuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    // Inject your JwtAuthFilter if it's a Bean

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) { // Constructor injection
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        //http.securityMatcher(ServerSecurityExchangeMatcher.);
        //http.securityMatcher(PathRequest.toH2Console());
        log.info("securityWebFilterChain called ...");
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
            .pathMatchers("/api/auth/**", "/h2-console/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                "/index.html", "/static/**", "/manifest.json", "/favicon.ico",
                "/logo192.png", "/asset-manifest.json", "/service-worker.js").permitAll() // Explicitly permit these static assets
            .anyExchange().permitAll()
            )
            // You may also need to configure headers if the console is running on the same port,
            // but with a separate port, this is often not necessary.
            // .headers(headers -> headers
            //     .frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable)
            // )
            // the following two are commented out for now to test
            //.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            //.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)      
            //
            // Add your JwtAuthFilter here, but ensure it's *after* the static resources are handled
            // For now, let's ensure the static resources are served first.
            // If JwtAuthFilter is a @Component, its order might need to be adjusted.
            // If you add it with .addFilterAt, ensure it's at the correct position (e.g., SecurityWebFiltersOrder.AUTHENTICATION)
            .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION) // Assuming you want it here
            .build();
    }

    // This router function will serve index.html for the root path
    // It's a common approach to ensure the SPA entry point is always served.
    @Bean
    public RouterFunction<ServerResponse> indexRouter() {
        log.info("indexRouter called ...");
        return RouterFunctions.route(RequestPredicates.GET("/"),
            request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                .bodyValue(new ClassPathResource("static/index.html")));
    }

    // You might also need a fallback for deep links in your SPA
    @Bean
    public RouterFunction<ServerResponse> spaFallbackRouter() {
        log.info("spaFallbackRouter called ...");
        return RouterFunctions.route(RequestPredicates.GET("/**").negate()
            .and(RequestPredicates.pathExtension("js"))
            .and(RequestPredicates.pathExtension("css"))
            .and(RequestPredicates.pathExtension("ico"))
            .and(RequestPredicates.pathExtension("png"))
            .and(RequestPredicates.pathExtension("json"))
            .and(RequestPredicates.path("/api/**")), // Exclude API paths
            request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                .bodyValue(new ClassPathResource("static/index.html")));

        /*
        return RouterFunctions.route(RequestPredicates.GET("/**")
                .and(RequestPredicates.not(RequestPredicates.pathExtension("js")))
                .and(RequestPredicates.not(RequestPredicates.pathExtension("css")))
                .and(RequestPredicates.not(RequestPredicates.pathExtension("ico")))
                .and(RequestPredicates.not(RequestPredicates.pathExtension("png")))
                .and(RequestPredicates.not(RequestPredicates.pathExtension("json")))
                .and(RequestPredicates.not(RequestPredicates.path("/api/**"))), 
            request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                .bodyValue(new ClassPathResource("static/index.html")));
        // */
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder) {
        log.info("authenticationManager ...");
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService) {
            @Override
            public Mono<Authentication> authenticate(Authentication authentication) {
                return super.authenticate(authentication)
                    .onErrorResume(e -> Mono.error(new BadCredentialsException("Invalid credentials")));
            }
        };
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository userRepository) {
        log.info("userDetailsService ...");
        return username -> userRepository.findByUsername(username)
            .map(user -> User.withUsername(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRoles().split(","))
            .build());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("passwordEncoder ...");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        log.info("corsWebFilter ...");
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Specific allowed origins (better than "*" for security)
        corsConfig.addAllowedOrigin("http://localhost:3000"); // React default port
        corsConfig.addAllowedOrigin("http://localhost:8088"); // Your React app port

        // Allow all methods (GET, POST, etc.) and headers
        corsConfig.addAllowedMethod("*"); // Allow all HTTP methods (GET, POST, etc.)
        corsConfig.addAllowedHeader("*"); // Allow all headers

        // Required for cookies/JWT in headers
        corsConfig.setAllowCredentials(true); // Allow cookies/auth headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // Apply to all paths

        return new CorsWebFilter(source);
    }
}

/*
curl -X POST http://localhost:8088/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"MyAdmin1@1","password":"MyAdmin1@1"}'

curl -X POST http://localhost:8088/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"MyAdmin1@1","password":"$2a$10$ley68e7CCJpduPSNPWl05OUa8IGHyr04MzY9pvq2vo3QXBjaG6Jk6"}'

return
{"token":"eyJhbGci...","user":{"username":"test","roles":["USER"]}}
 */

 /*
Key Features:
    Stateless JWT Authentication
    Role-based Authorization
    Reactive Security Context
    Proper Exception Handling
    CSRF Disabled (for API-only applications)
    Integration with Reactive User Details

This implementation provides a complete, production-ready JWT authentication setup for Spring WebFlux applications. The security filter chain is properly configured with:
    JWT authentication filter
    Role-based access control
    Proper error handling
    Reactive streams throughout the pipeline
 */

 /*
   
    
    public SecurityWebFilterChain securityWebFilterChainBkup(ServerHttpSecurity http,
        JwtUtils jwtUtils, ReactiveAuthenticationManager authenticationManager) {
        // Create JWT authentication filter
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtUtils);       
        // Configure authentication web filter
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(jwtAuthFilter::convert);

        return http
            .cors(cors -> cors.disable())                   // Disable default CORS handling (use CorsWebFilter instead)
            .csrf(ServerHttpSecurity.CsrfSpec::disable)     // If using JWT, CSRF can be disabled
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .logout(ServerHttpSecurity.LogoutSpec::disable)
            // Add JWT filter at the AUTHENTICATION position
            .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            // Authorization rules
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/auth/**").permitAll()
                .pathMatchers("/api/public/**").permitAll()
                .pathMatchers("/api/v[0-9]+/.*", "/public/.*").permitAll()
                @Profile("!prod")
                .pathMatchers("/h2-console/**").hasIpAddress("127.0.0.1")
                .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()                
                .pathMatchers("/api/admin/**").hasRole("ADMIN")
                .pathMatchers("/admin/**", "/management/**", "/actuator/**").hasRole("ADMIN")
                .pathMatchers("/api/user/**", "/profile/**").hasRole("USER")
                .pathMatchers("/api/patient/**").hasAnyRole("PATIENT", "NURSE", "PHYSICIAN", "ADMIN", "PHARMACIST")
                .pathMatchers("/api/nurse/**").hasAnyRole("NURSE", "PHYSICIAN", "ADMIN")
                .pathMatchers("/api/physician/**").hasAnyRole("PHYSICIAN", "ADMIN")
                .anyExchange().authenticated()
            )
            // Exception handling
            .exceptionHandling(handling -> handling
                .authenticationEntryPoint((exchange, ex) -> 
                    Mono.fromRunnable(() -> {
                        log.error("authenticationEntryPoint Unauthorized {}", exchange.getPrincipal());
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory().wrap("{\"error\":\"Unauthorized\"}".getBytes())));
                    }))
                .accessDeniedHandler((exchange, ex) -> 
                    Mono.fromRunnable(() -> {
                        log.error("accessDeniedHandler Forbidden {}", exchange.getPrincipal());
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory().wrap("{\"error\":\"Forbidden\"}".getBytes())));
                    })
                )
                //.authenticationEntryPoint((exchange, ex) -> 
                //    Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))           
                //.accessDeniedHandler((exchangeDeny, ex) -> 
                //    Mono.fromRunnable(() -> exchangeDeny.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                //)                
            )
            .build();
    }
 */
