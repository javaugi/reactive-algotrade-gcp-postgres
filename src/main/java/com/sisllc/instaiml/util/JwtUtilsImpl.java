/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sisllc.instaiml.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import static java.lang.StrictMath.log;
import java.util.Date;
import java.util.HashMap;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
@Component
public class JwtUtilsImpl implements JwtUtils {

    private final SecretKey SECRET_KEY = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
    private final long EXPIRATION_MS = 86400000; // 24 hours

    /*
    @Override
    public Mono<Boolean> validateToken(String token) {
        log.info("Incoming token: {}", token);
        return Mono.fromCallable(() -> {
            try {
                Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parse(token);
                return true;
            } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SecurityException | IllegalArgumentException e) {
                return false;
            }
        });
    }
    // */
    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> {
            try {
                Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .after(new Date());
                return true;
            } catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | SecurityException | IllegalArgumentException e) {
                return false;
            }
        });
    }

    @Override
    public Mono<Authentication> getAuthentication(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(SECRET_KEY)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        String username = claims.getSubject();
        List<?> rawRoles = claims.get("roles", List.class);
        List<SimpleGrantedAuthority> authorities = rawRoles.stream()
            .map(Object::toString)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        return Mono.just(new UsernamePasswordAuthenticationToken(username, null, authorities));
    }

    /*
    public Mono<Authentication> getAuthentication(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
            
            String username = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);
            
            return new UsernamePasswordAuthenticationToken(
                username,
                null,
                roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList()));
        });
    }        
   // */
    @Override
    public Mono<String> generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        return Mono.just(Jwts.builder()
            .claims(claims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
            .signWith(SECRET_KEY)
            .compact());
    }

    @Override
    public Mono<String> generateToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));

        return Mono.just(Jwts.builder()
            .claims(claims)
            .subject(authentication.getName())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
            .signWith(SECRET_KEY)
            .compact());
    }

    @Override
    public Mono<Boolean> validateToken(String token, UserDetails userDetails) {
        return Mono.fromCallable(() -> {
            final String username = extractUsername(token);
            return (userDetails.getUsername().equals(username) && !isTokenExpired(token));
        });
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .decryptWith(SECRET_KEY)
            .build()
            .parseEncryptedClaims(token)
            .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
