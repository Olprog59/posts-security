package com.formation.blog_security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:1800000}")
    private long expirationTime;

    private SecretKey key;

    private final UserDetailsService userDetailsService;

    public JwtService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    private void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Map<String, String> generateToken(String email) {
        UserDetails user = userDetailsService.loadUserByUsername(email);
        return this.generateJwt(user);
    }

    private Map<String, String> generateJwt(UserDetails user) {
        // heure création (ms)
        final long currentTime = System.currentTimeMillis();

        // heure expiration (ms) + 30 minutes
//        final long expirationTime = currentTime + 30 * 60 * 1000;
        expirationTime += currentTime;

        log.warn("Expiration time: {}", new Date(expirationTime));

        // data => claims
        Map<String, Object> claims = Map.of(
                "email", user.getUsername(),
                Claims.EXPIRATION, new Date(expirationTime),
                Claims.SUBJECT, user.getUsername()
        );

        // JWT dépendance
        // générer le token avec Jwts
        final String token = Jwts.builder()
                .issuedAt(new Date(currentTime))
                .expiration(new Date(expirationTime))
                .subject(user.getUsername())
                .claims(claims)
                .signWith(key)
                .compact();

        return Map.of("bearer", token);
    }

    public String extractUsername(String jwtToken) {
        return getPayload(jwtToken).getSubject();
    }

    public boolean isTokenExpired(String jwtToken) {
        Date expiration = new Date();
        return expiration.after(getPayload(jwtToken).getExpiration());
    }

    private Claims getPayload(String jwtToken) {
        return (Claims) Jwts.parser().verifyWith(key).build().parse(jwtToken).getPayload();
    }
}
