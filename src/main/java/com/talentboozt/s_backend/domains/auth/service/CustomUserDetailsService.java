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

    @Autowired
    private com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository eUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CredentialsModel credentials = credentialsService.getCredentialsByEmail(email);
        
        if (credentials != null) {
            java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();
            if (credentials.getRoles() != null) {
                for (String role : credentials.getRoles()) {
                    authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
                    if (!role.startsWith("ROLE_")) authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role));
                }
            }
            if (credentials.getPlatformRole() != null) {
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(credentials.getPlatformRole()));
                if (!credentials.getPlatformRole().startsWith("ROLE_")) authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + credentials.getPlatformRole()));
            }
            if (credentials.getPermissions() != null) {
                for (String p : credentials.getPermissions()) authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(p));
            }

            return new CustomUserDetails(credentials.getEmployeeId(), credentials.getEmail(), 
                    credentials.getPassword() != null ? credentials.getPassword() : "", authorities, credentials.getActiveWorkspaceId());
        }

        // Fallback to EDU user
        com.talentboozt.s_backend.domains.edu.model.EUser eUser = eUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();
        if (eUser.getRoles() != null) {
            for (com.talentboozt.s_backend.domains.edu.enums.ERoles role : eUser.getRoles()) {
                String roleName = role.name();
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(roleName));
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + roleName));
            }
        }

        return new CustomUserDetails(eUser.getId(), eUser.getEmail(), 
                eUser.getPasswordHash() != null ? eUser.getPasswordHash() : "", authorities, null);
    }

}
