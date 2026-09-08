package com.digitaladvertisingmanagement.auth.infrastructure.security;

import com.digitaladvertisingmanagement.auth.application.security.AuthenticatedUser;
import com.digitaladvertisingmanagement.auth.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final String secret;
  private final long expirationMs;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.expiration-ms}") long expirationMs) {
    this.secret = secret;
    this.expirationMs = expirationMs;
  }

  public String generateToken(User user) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + expirationMs);

    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    return Jwts.builder()
        .subject(user.getEmail())
        .claim("userId", user.getId())
        .issuedAt(now)
        .expiration(expiry)
        .signWith(key)
        .compact();
  }

  public boolean isValid(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
      Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
      return true;
    } catch (JwtException | IllegalArgumentException exception) {
      return false;
    }
  }

  public AuthenticatedUser extractAuthenticatedUser(String token) {
    Claims claims = extractClaims(token);

    Long userId = claims.get("userId", Number.class).longValue();
    String email = claims.getSubject();

    return new AuthenticatedUser(userId, email);
  }

  private Claims extractClaims(String token) {
    SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
  }
}
