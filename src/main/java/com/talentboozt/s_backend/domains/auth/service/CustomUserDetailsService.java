package com.talentboozt.s_backend.domains.auth.service;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CredentialsService credentialsService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CredentialsModel credentials = credentialsService.getCredentialsByEmail(email);
        if (credentials == null) {
            throw new UsernameNotFoundException("User not found");
        }

        String password = credentials.getPassword() != null ? credentials.getPassword() : "";
        return new CustomUserDetails(
                credentials.getEmployeeId(),
                credentials.getEmail(),
                password,
                Collections.emptyList());
    }
}
