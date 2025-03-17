package com.talentboozt.s_backend.Service.common.security;

import com.talentboozt.s_backend.Model.common.security.TokenModel;
import com.talentboozt.s_backend.Repository.common.security.TokenRepository;
import com.talentboozt.s_backend.Utils.ConfigUtility;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Service
public class ValidateTokenService {

    @Autowired
    private ConfigUtility configUtil;

    @Autowired
    private TokenRepository tokenRepository;

    public String generateToken(String username) {
        SecretKey secretKey = Keys.hmacShaKeyFor(configUtil.getProperty("TOKEN_SECRET").getBytes());
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .signWith(secretKey)
                .compact();

        TokenModel tokenModel = new TokenModel();
        tokenModel.setToken(token);
        tokenModel.setUsername(username);
        tokenModel.setExpiration(new Date(System.currentTimeMillis() + 86400000)); // 24 hours
        tokenModel.setUsed(false);
        tokenRepository.save(tokenModel);

        return token;
    }

    public boolean validateToken(String token) {
        Optional<TokenModel> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isPresent()) {
            TokenModel tokenModel = optionalToken.get();

            // Check if token is already used
            if (tokenModel.isUsed()) {
//                System.out.println("Token is already used");
                return false;
            }

            // Check if token is expired
            if (tokenModel.getExpiration().before(new Date())) {
                //System.out.println("Token is expired");
                return false;
            }

            try {
                JwtParser parser = Jwts.parserBuilder()
                        .setSigningKey(Keys.hmacShaKeyFor(configUtil.getProperty("TOKEN_SECRET").getBytes()))
                        .build();
                parser.parseClaimsJws(token);

                // Mark the token as used
                tokenModel.setUsed(true);
                tokenRepository.save(tokenModel);

//                System.out.println("Token is valid and marked as used");
                return true;

            } catch (Exception e) {
//                System.out.println("Token is invalid");
                e.printStackTrace();
                return false;
            }
        } else {
//            System.out.println("Token does not exist in database");
            return false;
        }
    }
}

