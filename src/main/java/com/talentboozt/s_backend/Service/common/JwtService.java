package com.talentboozt.s_backend.Service.common;

import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
            Jwts.parser().setSigningKey(token).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
