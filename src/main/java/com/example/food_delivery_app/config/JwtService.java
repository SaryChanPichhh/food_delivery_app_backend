package com.example.food_delivery_app.config;

import com.example.food_delivery_app.dto.repsonse.LoginResDto;
import com.example.food_delivery_app.dto.request.LoginReqDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Generate SecretKey
    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String generateToken(LoginResDto model) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", model.getUserId());
        claims.put("userName", model.getUserName());
        claims.put("email", model.getEmail());
        claims.put("firstName", model.getFirstName());
        claims.put("lastName", model.getLastName());

        return Jwts.builder()
                .subject(model.getUserName())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();
    }
    public String extractUsername(String token) {

        return getClaims(token).getSubject();
    }
    public Integer extractUserId(String token) {

        return getClaims(token)
                .get("userId", Integer.class);
    }
    // Validate Token
    public boolean isTokenValid(String token) {

        return !getClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // Get Claims
    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
