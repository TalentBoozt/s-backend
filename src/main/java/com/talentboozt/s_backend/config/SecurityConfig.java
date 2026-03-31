package com.talentboozt.s_backend.config;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.domains.auth.service.CustomUserDetailsService;
import com.talentboozt.s_backend.shared.security.cfg.JwtAuthenticationFilter;
import com.talentboozt.s_backend.shared.utils.ConfigUtility;
import com.talentboozt.s_backend.shared.utils.EncryptionUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CredentialsService credentialsService;
    private final ConfigUtility configUtil;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomUserDetailsService userDetailsService,
            ConfigUtility configUtil, CredentialsService credentialsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.configUtil = configUtil;
        this.credentialsService = credentialsService;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authProvider);
    }

    @Bean
    @Order(0) // Higher priority than main security chain
    public SecurityFilterChain webSocketSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/ws/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**").permitAll());

        return http.build();
    }

    @Bean
    @Order(1) // Specific matcher before catch-all
    public SecurityFilterChain captchaSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/security/verify-captcha")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/security/verify-captcha").permitAll());

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                        .xssProtection(HeadersConfigurer.XXssConfig::disable)
                        .referrerPolicy(referrerPolicy -> referrerPolicy
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .httpStrictTransportSecurity(
                                httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer
                                        .includeSubDomains(false)
                                        .maxAgeInSeconds(31536000)))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> {
                    // ── Actuator Security ──────────────────────────────────────
                    String actuatorSensitive = configUtil.getProperty("SECURITY_ACTUATOR_SENSITIVE");
                    if (actuatorSensitive != null && !actuatorSensitive.isEmpty()) {
                        for (String endpoint : actuatorSensitive.split(",")) {
                            auth.requestMatchers("/actuator/" + endpoint.trim()).authenticated();
                        }
                    }
                    auth.requestMatchers("/actuator/health", "/actuator/info").permitAll();

                    // ── EDU Domain Security ────────────────────────────────────
                    auth.requestMatchers(HttpMethod.GET, "/api/edu/admin/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/admin/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/edu/enrollments/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/edu/notifications/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/edu/finance/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/edu/analytics/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/monetization/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/edu/courses/creator/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/edu/progress/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/edu/quizzes/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/edu/assignments/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/edu/certificates/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/edu/personalization/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/edu/workspaces/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/edu/subscriptions/**").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/edu/trust/reports/**").authenticated();

                    // ── Public Endpoints ────────────────────────────────────
                    String publicPaths = configUtil.getProperty("SECURITY_PUBLIC_PATHS");
                    if (publicPaths != null && !publicPaths.isEmpty()) {
                        for (String path : publicPaths.split(",")) {
                            auth.requestMatchers(path.trim()).permitAll();
                        }
                    }
                    
                    auth.requestMatchers(HttpMethod.GET, "/**").permitAll();

                    // ── Legacy V2 Write Protection ──────────────────────────
                    auth.requestMatchers(HttpMethod.POST, "/api/v2/posts/**").authenticated()
                            .requestMatchers(HttpMethod.PUT, "/api/v2/posts/**").authenticated()
                            .requestMatchers(HttpMethod.DELETE, "/api/v2/posts/**").authenticated()
                            .requestMatchers(HttpMethod.POST, "/api/v2/communities/**").authenticated()
                            .requestMatchers(HttpMethod.PUT, "/api/v2/communities/**").authenticated()
                            .requestMatchers(HttpMethod.DELETE, "/api/v2/communities/**").authenticated()
                            .requestMatchers(HttpMethod.POST, "/api/v2/messaging/**").authenticated()
                            .requestMatchers(HttpMethod.PUT, "/api/v2/messaging/**").authenticated()
                            .requestMatchers(HttpMethod.DELETE, "/api/v2/messaging/**").authenticated()
                            .requestMatchers(HttpMethod.POST, "/api/v2/employee/**").authenticated()
                            .requestMatchers(HttpMethod.PUT, "/api/v2/employee/**").authenticated()
                            .requestMatchers(HttpMethod.PUT, "/api/v2/emp_**").authenticated()
                            .requestMatchers(HttpMethod.POST, "/api/v2/emp_**").authenticated()
                            .requestMatchers(HttpMethod.DELETE, "/api/v2/emp_**").authenticated()
                            .requestMatchers(HttpMethod.POST, "/api/v2/notifications/**").authenticated()
                            .requestMatchers(HttpMethod.PUT, "/api/v2/notifications/**").authenticated()
                            .requestMatchers(HttpMethod.POST, "/api/v2/reports").authenticated()
                            .requestMatchers("/api/v2/reports/**").authenticated();

                    // ── Auth-Required Modules ───────────────────────────────
                    auth.requestMatchers("/api/v2/resumes/ai/**").authenticated()
                            .requestMatchers("/api/v2/resumes/**").authenticated()
                            .requestMatchers("/api/lifeplanner/users").permitAll()
                            .requestMatchers("/api/lifeplanner/stripe/webhook").permitAll()
                            .requestMatchers("/api/monetization/stripe/webhook").permitAll()
                            .requestMatchers("/api/lifeplanner/**").authenticated();

                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                        .loginPage(configUtil.getProperty("FAILURE_REDIRECT"))
                        .defaultSuccessUrl(configUtil.getProperty("SUCCESS_REDIRECT"), true)
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage(configUtil.getProperty("FAILURE_REDIRECT"))
                        .userInfoEndpoint(userInfo -> {
                            userInfo.oidcUserService(this.oidcUserService());
                            userInfo.userService(this.githubUserService());
                            userInfo.userService(this.facebookUserService());
                            userInfo.userService(this.linkedinUserService());
                        })
                        .defaultSuccessUrl(configUtil.getProperty("GOOGLE_CLIENT_REDIRECT"), true)
                        .permitAll());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        
        // Dynamic Origin Loading
        String origins = configUtil.getProperty("ALLOWED_ORIGINS");
        if (origins != null && !origins.isEmpty()) {
            configuration.setAllowedOriginPatterns(List.of(origins.split(",")));
        } else {
            // Fallback for development if .env is missing
            configuration.setAllowedOriginPatterns(List.of("http://localhost:4200", "http://localhost:3000"));
        }

        // Dynamic Methods Loading
        String methods = configUtil.getProperty("ALLOWED_METHODS");
        configuration.setAllowedMethods(methods != null ? List.of(methods.split(",")) : List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Dynamic Headers Loading
        String headers = configUtil.getProperty("ALLOWED_HEADERS");
        configuration.setAllowedHeaders(headers != null ? List.of(headers.split(",")) : List.of("*"));

        // Dynamic Exposed Headers
        String exposedHeaders = configUtil.getProperty("EXPOSED_HEADERS");
        configuration.setExposedHeaders(exposedHeaders != null ? List.of(exposedHeaders.split(",")) : List.of("X-XSRF-TOKEN", "x-user-id"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                try {
                    return EncryptionUtility.encrypt(rawPassword.toString()); // Encrypt password before storing
                } catch (Exception e) {
                    throw new RuntimeException("Password encryption failed", e);
                }
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                try {
                    String decryptedPassword = EncryptionUtility.decrypt(encodedPassword);
                    return decryptedPassword.equals(rawPassword.toString()); // Compare decrypted password
                } catch (Exception e) {
                    return false;
                }
            }
        };
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
