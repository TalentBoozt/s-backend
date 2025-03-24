package com.talentboozt.s_backend.Service.common;

import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
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

        return new User(credentials.getEmail(), credentials.getPassword(), Collections.emptyList());
    }
}
