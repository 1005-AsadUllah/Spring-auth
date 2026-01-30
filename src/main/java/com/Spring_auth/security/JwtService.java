package com.Spring_auth.security;

import com.Spring_auth.enitity.Role;
import com.Spring_auth.enitity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Getter
@Setter
public class JwtService {

    @Value("${security.jwt.secret}")
    private String key;

    @Value("${security.jwt.expiration}")
    private Long expiration;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.refresh-ttl-seconds}")
    private Long refreshTtlSeconds;

    @Getter
    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        if (key == null || key.length() < 32) {
            throw new IllegalStateException(
                    "JWT secret key must be at least 32 characters"
            );
        }
        this.signingKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        List<String> roles = user.getRoles() == null ? List.of() :
                user.getRoles().stream().map(Role::getRole).toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expiration)))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "typ", "access"
                ))
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken(User user, String jti){
        Instant now = Instant.now();

        return Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .claim("typ", "refresh")
                .signWith(signingKey)
                .compact();

    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token);
    }

    public boolean isAccessToken(String token){
        Claims claims = parse(token).getPayload();
        return "access".equals(claims.get("typ"));
    }

    public boolean isRefreshToken(String token){
        Claims claims = parse(token).getPayload();
        return "refresh".equals(claims.get("typ"));
    }

    public Long getUserIdFromToken(String token){
        Claims claims = parse(token).getPayload();
        return Long.parseLong(claims.getSubject());
    }

    public String getJtiFromToken(String token){
        return parse(token).getPayload().getId();
    }
}
