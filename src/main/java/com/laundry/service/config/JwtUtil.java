package com.laundry.service.config;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.laundry.service.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // Secret key must be at least 32 bytes for HS256
    private static final String SECRET = "myverysecretkeythatis32byteslong!"; // 32 bytes
    private static final long EXPIRATION_MS = 86400000; // 1 day

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * Generate a JWT token for the given email.
     */
    public String generateToken(User user) {
        return Jwts.builder()
//                .setSubject(email)
        		.setSubject(user.getEmail())
        	//	.setSubject(user.getId().toString())
                .claim("user_id", user.getId().toString())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract the email (subject) from the JWT token.
     */
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
	/**
	 * Extract the user claims from the JWT token.
	 */
    public String extractClaim(String token, String claimKey) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // key should be of type java.security.Key
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(claimKey, String.class);
    }

    /**
     * Validate the token and ensure it is not expired.
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}