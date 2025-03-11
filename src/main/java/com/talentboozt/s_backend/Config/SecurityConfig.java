package com.talentboozt.s_backend.Config;

import com.talentboozt.s_backend.Model.CredentialsModel;
import com.talentboozt.s_backend.Service.CredentialsService;
import com.talentboozt.s_backend.Utils.ConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private ConfigUtility configUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .contentSecurityPolicy(policy -> policy
                                .policyDirectives("default-src 'self'; " +
                                        "script-src 'self' https://cdn.jsdelivr.net 'unsafe-inline'; " +
                                        "style-src 'self' https://fonts.googleapis.com https://cdn.jsdelivr.net 'unsafe-inline'; " +
                                        "font-src 'self' https://fonts.gstatic.com https://kit.fontawesome.com; " +
                                        "img-src 'self' data:; " +
                                        "connect-src 'self' https://firebaseinstallations.googleapis.com; " +
                                        "frame-src 'none'; " +
                                        "object-src 'none'; " +
                                        "base-uri 'self'; " +
                                        "form-action 'self'; " +
                                        "script-src-elem 'self' https://cdn.jsdelivr.net 'unsafe-inline'; " +
                                        "style-src-elem 'self' https://fonts.googleapis.com https://cdn.jsdelivr.net 'unsafe-inline'")
                        )
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .xssProtection(HeadersConfigurer.XXssConfig::disable)
                        .referrerPolicy(referrerPolicy -> referrerPolicy.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .httpStrictTransportSecurity(
                                httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer
                                        .includeSubDomains(false)
                                        .maxAgeInSeconds(31536000)
                        )
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/stripe/**", "/actuator/**", "/public/**",
                                "/login", "/oauth2/**", "/oauth/**",
                                "/sitemap.xml"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(withDefaults())
                .formLogin(form -> form
                        .loginPage(configUtil.getProperty("FAILURE_REDIRECT"))
                        .defaultSuccessUrl(configUtil.getProperty("SUCCESS_REDIRECT"), true)
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage(configUtil.getProperty("FAILURE_REDIRECT"))
                        .userInfoEndpoint(userInfo -> {
                            userInfo.oidcUserService(this.oidcUserService());
                            userInfo.userService(this.githubUserService());
                            userInfo.userService(this.facebookUserService());
                            userInfo.userService(this.linkedinUserService());
                        })
                        .defaultSuccessUrl(configUtil.getProperty("GOOGLE_CLIENT_REDIRECT"), true)
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of(
                configUtil.getProperty("ALLOWED_ORIGIN_1"),
                configUtil.getProperty("ALLOWED_ORIGIN_2"),
                configUtil.getProperty("ALLOWED_ORIGIN_3"),
                configUtil.getProperty("ALLOWED_ORIGIN_4"),
                configUtil.getProperty("ALLOWED_ORIGIN_5"),
                configUtil.getProperty("ALLOWED_ORIGIN_6")
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Stripe-Signature", "X-Demo-Mode"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public UserDetailsService users() {
        PasswordEncoder encoder = passwordEncoder();
        UserDetails user = User.builder()
                .username(configUtil.getProperty("USER2_USERNAME"))
                .password(encoder.encode(configUtil.getProperty("USER2_PASSWORD")))
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username(configUtil.getProperty("USER1_USERNAME"))
                .password(encoder.encode(configUtil.getProperty("USER1_PASSWORD")))
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private OidcUserService oidcUserService() {
        OidcUserService delegate = new OidcUserService();
        return new OidcUserService() {
            @Override
            public OidcUser loadUser(OidcUserRequest userRequest) {
                OidcUser oidcUser = delegate.loadUser(userRequest);
                String email = oidcUser.getEmail();
                String firstName = oidcUser.getGivenName();
                String lastName = oidcUser.getFamilyName();

                // Custom user registration logic
                CredentialsModel existingCredentials = credentialsService.getCredentialsByEmail(email);
                if (existingCredentials != null) {
                    return loginUser(existingCredentials);
                } else {
                    return registerGoogleUser(email, firstName, lastName);
                }
            }
        };
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> githubUserService() {
        return new OAuth2UserService<>() {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new OAuth2UserService<>() {
                    @Override
                    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
                        return null;
                    }
                };
                OAuth2User user = delegate.loadUser(userRequest);

                // Extract user information (like email, name) from GitHub
                String email = user.getAttribute("email");
                String name = user.getAttribute("name");

                // Custom user registration logic (similar to Google)
                CredentialsModel existingCredentials = credentialsService.getCredentialsByEmail(email);
                if (existingCredentials != null) {
                    return loginUser(existingCredentials);
                } else {
                    return registerGitHubUser(email, name);
                }
            }
        };
    }

    private OidcUser registerGitHubUser(String email, String name) {
        CredentialsModel newUser = new CredentialsModel();
        newUser.setEmail(email);
        newUser.setFirstname(name); // Optionally split the name if needed
        newUser.setLastname(""); // You can customize this if needed
        newUser.setRole("candidate");
        newUser.setUserLevel("1");
        CredentialsModel savedUser = credentialsService.addCredentials(newUser, null, null);
        return (OidcUser) savedUser; // Adjust if you want to return a different type
    }

    private OidcUser loginUser(CredentialsModel credentials) {
        credentials.setRole("candidate");
        credentials.setUserLevel("1");
        return (OidcUser) credentials;
    }

    private OidcUser registerGoogleUser(String email, String firstName, String lastName) {
        CredentialsModel newUser = new CredentialsModel();
        newUser.setEmail(email);
        newUser.setFirstname(firstName);
        newUser.setLastname(lastName);
        newUser.setRole("candidate");
        newUser.setUserLevel("1");
        CredentialsModel savedUser = credentialsService.addCredentials(newUser, null, null);
        return (OidcUser) savedUser;
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> facebookUserService() {
        return userRequest -> {
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
            OAuth2User oauth2User = delegate.loadUser(userRequest);

            // Extract user details
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String[] nameParts = name.split(" ", 2);

            // Custom logic for registration or login
            CredentialsModel existingCredentials = credentialsService.getCredentialsByEmail(email);
            if (existingCredentials != null) {
                return loginUser(existingCredentials);
            } else {
                return registerFacebookUser(email, nameParts[0], nameParts.length > 1 ? nameParts[1] : "");
            }
        };
    }

    private OidcUser registerFacebookUser(String email, String firstName, String lastName) {
        CredentialsModel newUser = new CredentialsModel();
        newUser.setEmail(email);
        newUser.setFirstname(firstName);
        newUser.setLastname(lastName);
        newUser.setRole("candidate");
        newUser.setUserLevel("1");
        return (OidcUser) credentialsService.addCredentials(newUser, null, null);
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> linkedinUserService() {
        return userRequest -> {
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
            OAuth2User oauth2User = delegate.loadUser(userRequest);

            // Extract user details
            String email = oauth2User.getAttribute("email");
            String name = oauth2User.getAttribute("name");
            String[] nameParts = name.split(" ", 2);

            // Custom logic for registration or login
            CredentialsModel existingCredentials = credentialsService.getCredentialsByEmail(email);
            if (existingCredentials != null) {
                return loginUser(existingCredentials);
            } else {
                return registerLinkedinUser(email, nameParts[0], nameParts.length > 1 ? nameParts[1] : "");
            }
        };
    }

    private OidcUser registerLinkedinUser(String email, String firstName, String lastName) {
        CredentialsModel newUser = new CredentialsModel();
        newUser.setEmail(email);
        newUser.setFirstname(firstName);
        newUser.setLastname(lastName);
        newUser.setRole("candidate");
        newUser.setUserLevel("1");
        return (OidcUser) credentialsService.addCredentials(newUser, null, null);
    }

}
