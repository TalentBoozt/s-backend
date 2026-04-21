package com.talentboozt.s_backend.config;

import com.talentboozt.s_backend.shared.utils.EncryptionUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                try {
                    return EncryptionUtility.encrypt(rawPassword.toString());
                } catch (Exception e) {
                    throw new RuntimeException("Password encryption failed", e);
                }
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                try {
                    String decryptedPassword = EncryptionUtility.decrypt(encodedPassword);
                    return decryptedPassword.equals(rawPassword.toString());
                } catch (Exception e) {
                    return false;
                }
            }
        };
    }
}
