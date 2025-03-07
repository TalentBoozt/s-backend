package com.talentboozt.s_backend.Controller;

import com.talentboozt.s_backend.Model.CredentialsModel;
import com.talentboozt.s_backend.Repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class OAuth2LoginController {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @GetMapping("/oauth2/success")
    public String getLoginInfo(@AuthenticationPrincipal OidcUser oidcUser) {
        String email = oidcUser.getEmail();
        Optional<CredentialsModel> user = Optional.ofNullable(credentialsRepository.findByEmail(email));

        if (user.isPresent()) {
            // Logic for existing user
        } else {
            // Register the user
            CredentialsModel newUser = new CredentialsModel();
            newUser.setEmail(email);
            newUser.setFirstname(oidcUser.getGivenName());
            newUser.setLastname(oidcUser.getFamilyName());
            newUser.setRole("candidate");
            credentialsRepository.save(newUser);
        }

        return "Login successful!";
    }

    // Fetch user profile
    @GetMapping("/user/profile")
    public CredentialsModel getUserProfile(@AuthenticationPrincipal OidcUser oidcUser) {
        String email = oidcUser.getEmail();
        return credentialsRepository.findByEmail(email);
    }
}
