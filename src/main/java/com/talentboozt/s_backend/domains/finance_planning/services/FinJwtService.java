package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.models.FinUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class FinJwtService {

    @Value("${jwt-token.secret:TalnovaFinanceSecretKeyThatIsVeryLongAndSecure12345!}")
    private String tokenSecret;

    @Value("${jwt-cookie.domain:talnova.io}")
    private String cookieDomain;

    private final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hours
    private final long REFRESH_EXPIRE_DURATION = 30L * 24 * 60 * 60 * 1000; // 30 days

    public ResponseCookie generateAccessTokenCookie(FinUser user) {
        String token = generateToken(user);
        return ResponseCookie.from("fin_access_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain(cookieDomain)
                .maxAge(EXPIRE_DURATION / 1000)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie generateRefreshTokenCookie(FinUser user) {
        String token = generateRefreshToken(user);
        return ResponseCookie.from("fin_refresh_token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .domain(cookieDomain)
                .maxAge(REFRESH_EXPIRE_DURATION / 1000)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie getCleanAccessTokenCookie() {
        return ResponseCookie.from("fin_access_token", null)
                .path("/")
                .domain(cookieDomain)
                .maxAge(0)
                .build();
    }

    public ResponseCookie getCleanRefreshTokenCookie() {
        return ResponseCookie.from("fin_refresh_token", null)
                .path("/")
                .domain(cookieDomain)
                .maxAge(0)
                .build();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(tokenSecret.getBytes());
    }

    public String generateToken(FinUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(FinUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRE_DURATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
