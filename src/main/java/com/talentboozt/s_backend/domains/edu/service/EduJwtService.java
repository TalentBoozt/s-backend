package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.EUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class EduJwtService {

    @Value("${jwt-token.secret:TalnovaEduSecretKeyThatIsVeryLongAndSecure12345!}")
    private String tokenSecret;

    @Value("${jwt-cookie.secure:false}")
    private boolean isSecure;

    @Value("${jwt-cookie.type:Lax}")
    private String cookieType;

    private final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hours
    private final long REFRESH_EXPIRE_DURATION = 30L * 24 * 60 * 60 * 1000; // 30 days

    public ResponseCookie generateAccessTokenCookie(EUser user) {
        String token = generateToken(user);
        return ResponseCookie.from("edu_access_token", token)
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(EXPIRE_DURATION / 1000)
                .sameSite(cookieType)
                .build();
    }

    public ResponseCookie generateRefreshTokenCookie(EUser user) {
        String token = generateRefreshToken(user);
        return ResponseCookie.from("edu_refresh_token", token)
                .httpOnly(true)
                .secure(isSecure)
                .path("/")
                .maxAge(REFRESH_EXPIRE_DURATION / 1000)
                .sameSite(cookieType)
                .build();
    }

    public ResponseCookie getCleanAccessTokenCookie() {
        return ResponseCookie.from("edu_access_token", null)
                .path("/")
                .maxAge(0)
                .build();
    }

    public ResponseCookie getCleanRefreshTokenCookie() {
        return ResponseCookie.from("edu_refresh_token", null)
                .path("/")
                .maxAge(0)
                .build();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(tokenSecret.getBytes());
    }

    public String generateToken(EUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roles", user.getRoles());
        claims.put("email", user.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(EUser user) {
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

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
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

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
