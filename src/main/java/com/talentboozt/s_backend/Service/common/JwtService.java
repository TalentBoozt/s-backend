package com.talentboozt.s_backend.Service.common;

import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt-token.secret}")
    private String token;

    public String generateToken(CredentialsModel user) {

        Key key = Keys.hmacShaKeyFor(token.getBytes());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getEmployeeId());
        claims.put("userLevel", user.getUserLevel());

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

    public String generateRefreshToken(CredentialsModel user) {
        // Refresh token should have a much longer expiration time, e.g., 30 days.
        Key key = Keys.hmacShaKeyFor(token.getBytes());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getEmployeeId());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 2592000000L)) // 30 days expiration
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
