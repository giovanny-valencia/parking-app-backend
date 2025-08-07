package com.parkingapp.backendapi.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Provides JWT token generation and validation services.
 *
 * <p>Parsing the token once per request and passing the Claims object to other methods.
 */
@Component
@Slf4j
public class JwtTokenProvider {

  @Value("${jwt.secret-key}")
  private String secretKey;

  @Value("${jwt.expiration-time}")
  private long expirationTime;

  /**
   * Generates a new JWT token for a given user.
   *
   * @param userDetails The user details to include in the token.
   * @return A new JWT token string.
   */
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();

    log.debug("User details: {}", userDetails);
    // todo: add userId to claims
    claims.put("Role", userDetails.getAuthorities());
    return generateToken(claims, userDetails);
  }

  /**
   * Performs the primary validation of the JWT token. This is the single, efficient point where the
   * token is parsed and validated.
   *
   * @param token The JWT token string.
   * @return The claims payload if the token is valid.
   * @throws ExpiredJwtException if the token has expired.
   * @throws SignatureException if the signature is invalid.
   * @throws MalformedJwtException if the token is not well-formed.
   */
  public Claims validateTokenAndGetClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  /**
   * Checks if a pre-parsed JWT token is valid for a given user. This method is now efficient as it
   * operates on an already parsed Claims object.
   *
   * @param claims The pre-parsed claims from the JWT.
   * @param userDetails The user details to validate against.
   * @return True if the token is valid, false otherwise.
   */
  public boolean isTokenValid(Claims claims, UserDetails userDetails) {
    final String username = claims.getSubject();
    final Date expiration = claims.getExpiration();
    return (username.equals(userDetails.getUsername()) && expiration.after(new Date()));
  }

  /**
   * Extracts the username subject from a pre-parsed Claims object.
   *
   * @param claims The pre-parsed claims from the JWT.
   * @return The username contained in the token's subject.
   */
  public String extractUsername(Claims claims) {
    return claims.getSubject();
  }

  // Helper functions

  private String generateToken(Map<String, Object> claims, UserDetails userDetails) {
    return Jwts.builder()
        .claims(claims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(getSigningKey())
        .compact();
  }

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
