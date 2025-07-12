package com.talentboozt.s_backend.shared.security.service;

import com.talentboozt.s_backend.domains.auth.dto.SSO.JwtUserPayload;
import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt-token.secret}")
    private String token;

    public String generateToken(JwtUserPayload user) {

        Key key = Keys.hmacShaKeyFor(token.getBytes());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId() == null ? "n/a" : user.getUserId());
        claims.put("userLevel", user.getUserLevel());
        claims.put("roles", user.getRoles());
        claims.put("permissions", user.getPermissions());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(this.token.getBytes())).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token has expired: " + e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT token: " + e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            System.out.println("Malformed JWT token: " + e.getMessage());
            return false;
        } catch (SignatureException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal argument while parsing JWT: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Error while parsing JWT: " + e.getMessage());
            return false;
        }
    }

    public String generateRefreshToken(JwtUserPayload user) {
        // Refresh token should have a much longer expiration time, e.g., 30 days.
        Key key = Keys.hmacShaKeyFor(token.getBytes());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 2592000000L)) // 30 days expiration
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public CredentialsModel getUserFromToken(String encodedToken) {
        SecretKey key = Keys.hmacShaKeyFor(token.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(encodedToken)
                .getBody();

        Date expiration = claims.getExpiration();
        if (expiration.before(new Date())) {
            throw new ExpiredJwtException(null, claims, "Token is expired");
        }

        CredentialsModel user = new CredentialsModel();
        user.setEmployeeId((String) claims.get("userId"));
        user.setEmail((String) claims.get("sub"));
        user.setUserLevel((String) claims.get("userLevel"));
        return user;
    }
}
