/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.security;

import com.sisllc.instaiml.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthFilter implements WebFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthFilter(JwtUtils jwtUtils) {
        log.info("JwtAuthFilter constructor ...");
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
       String path = exchange.getRequest().getURI().getPath();
        log.info("JwtAuthFilter filter called ... path={}", path);

        // IMPORTANT: Bypass filter logic for static resources
        // This is crucial. If the path starts with /static/, /index.html, etc.,
        // just pass it down the chain immediately.
        if (pathSpecial(path)) {
            log.debug("Bypassing JwtAuthFilter::filter for static resource: {}", path);
            return chain.filter(exchange);
        }
        
        log.info("filter called for non-static path: {}", path);
        return Mono.justOrEmpty(extractToken(exchange.getRequest()))
            .doOnNext(token -> log.info("Token found: {}", token))
            .flatMap(token -> jwtUtils.validateToken(token)
                .flatMap(valid -> {
                    log.info("filter valid {} token {}", valid, token);
                    if (valid) {
                        return jwtUtils.getAuthentication(token)
                            .doOnNext(auth -> log.info("Authenticated user: {}", auth.getName()))
                            .flatMap(auth -> chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)));
                    }
                    log.warn("Invalid token for path: {}", path);
                    return chain.filter(exchange); // Continue chain even for invalid tokens
                }))
            .switchIfEmpty(Mono.defer(() -> {
                log.warn("No token found, proceeding anonymously for path: {}", path);
                return chain.filter(exchange); // No token, just continue the chain
            }));
    }

    // You might not need convert method if you're not setting it as ServerAuthenticationConverter
    // Keep it if you explicitly set authenticationWebFilter.setServerAuthenticationConverter(jwtAuthFilter::convert);
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("convert called ... path={}", path);
        if (pathSpecial(path)) {
            log.debug("Bypassing JwtAuthFilter::convert for static resource: {}", path);
            return Mono.empty(); // Return empty to bypass authentication for static resources
        }
        return Mono.justOrEmpty(extractToken(exchange.getRequest()))
            .filterWhen(jwtUtils::validateToken)
            .flatMap(jwtUtils::getAuthentication);
    }

    private boolean pathSpecial(String path) {
        if (path.startsWith("/logo") || path.startsWith("/api/user") || path.startsWith("/api/auth") ||
            path.startsWith("/public") || path.startsWith("/static/") || path.startsWith("/index") || 
            path.startsWith("/service") || path.startsWith("/manifest") || path.startsWith("/favicon") || 
            path.startsWith("/asset-manifest")) {
            log.debug("return true for pathSpecial {}", path);
            return true;
        }        
        
        return false;
    }
    
    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        log.info("extractToken authHeader={} hasNext={}", authHeader, StringUtils.hasText(authHeader));
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}



/*

   @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.justOrEmpty(extractToken(exchange.getRequest()))
            .flatMap(token -> jwtUtils.validateToken(token)
                .flatMap(valid -> {
                    if (valid) {
                        return jwtUtils.getAuthentication(token)
                            .flatMap(auth -> chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)));
                    }
                    return chain.filter(exchange);
                }))
            .switchIfEmpty(chain.filter(exchange));
    }    

    //@Override
    public Mono<Void> filterOrig(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractToken(exchange.getRequest());
        log.info("filter extracted token={}", token);
        if (token == null) return chain.filter(exchange);
        
        return jwtUtils.getAuthentication(token)
            .flatMap(auth -> chain.filter(exchange)
            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
            .switchIfEmpty(chain.filter(exchange));
    }    
    
   
    @Override
    public Mono<Void> filter2(ServerWebExchange exchange, WebFilterChain chain) {
        return this.authenticationConverter.convert(exchange)
            .flatMap(authentication -> 
                chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)))
            .switchIfEmpty(chain.filter(exchange));
    }
    
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("Authorization"))
            .filter(authHeader -> authHeader.startsWith("Bearer "))
            .switchIfEmpty(Mono.error(new BadCredentialsException("Missing token")))
            .flatMap(token -> {
                String jwt = token.substring(7);
                // Validate token
                return jwtUtils.validateToken(jwt);
            })
            .doOnError(e -> log.error("JWT error: {}", e.getMessage()));
    }
    
*/
