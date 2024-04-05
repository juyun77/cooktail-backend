package io.cooktail.backend.global.security;

import io.cooktail.backend.domain.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenProvider {

  @Value("${spring.jwt.secretKey}")
  private String secretKey;

  public String generateJwtToken(Member member) {
    Date expiryDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
    byte[] secretKeyBytes = Base64.getEncoder().encode(secretKey.getBytes(StandardCharsets.UTF_8));

    return Jwts.builder()
        .signWith(SignatureAlgorithm.HS512, secretKeyBytes)
        .setSubject(member.getId().toString())
        .setIssuer("cooktail - app")
        .setIssuedAt(new Date())
        .setExpiration(expiryDate)
        .compact();
  }

  public String validateAndGetMemberId(String token) {
    byte[] secretKeyBytes = Base64.getEncoder().encode(secretKey.getBytes(StandardCharsets.UTF_8));

    Claims claims = Jwts.parser()
        .setSigningKey(secretKeyBytes)
        .parseClaimsJws(token)
        .getBody();

    return claims.getSubject();
  }
}
