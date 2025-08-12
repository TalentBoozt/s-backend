package com.talentboozt.s_backend.domains.auth.controller;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.shared.security.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v2/portal_credentials")
public class CredentialsController {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/add/{platform}")
    public CredentialsModel addCredentials(@RequestBody CredentialsModel credentials, @PathVariable String platform) {
        String referrer = credentials.getReferrerId();
        return credentialsService.addCredentials(credentials, platform, referrer);
    }

    @GetMapping("/getAll")
    public Iterable<CredentialsModel> getAllCredentials(HttpServletRequest request) {
        String extractToken = jwtService.extractTokenFromHeaderOrCookie(request);
        if (!jwtService.validateToken(extractToken)) return null;
        return credentialsService.getAllCredentials();
    }

    @GetMapping("/getUserCountByLevel/{level}")
    public long getUserCountByLevel(@PathVariable String level) {
        return credentialsService.getUserCountByLevel(level);
    }

    @GetMapping("/get/{id}")
    public Optional<CredentialsModel> getCredentials(HttpServletRequest request, @PathVariable String id) {
        String extractToken = jwtService.extractTokenFromHeaderOrCookie(request);
        if (!jwtService.validateToken(extractToken)) return Optional.empty();
        return credentialsService.getCredentials(id);
    }

    @GetMapping("/getByEmail/{email}")
    public CredentialsModel getCredentialsByEmail(HttpServletRequest request, @PathVariable String email) {
        String extractToken = jwtService.extractTokenFromHeaderOrCookie(request);
        if (!jwtService.validateToken(extractToken)) return null;
        return credentialsService.getCredentialsByEmail(email);
    }

    @GetMapping("/getByEmployeeId/{employeeId}")
    public Optional<CredentialsModel> getCredentialsByEmployeeId(HttpServletRequest request, @PathVariable String employeeId) {
        String extractToken = jwtService.extractTokenFromHeaderOrCookie(request);
        if (!jwtService.validateToken(extractToken)) return Optional.empty();
        return credentialsService.getCredentialsByEmployeeId(employeeId);
    }

    @PutMapping("/update/{employeeId}")
    public CredentialsModel updateCredentials(@PathVariable String employeeId, @RequestBody CredentialsModel credentials) {
        return credentialsService.updateCredentials(employeeId, credentials);
    }

    @DeleteMapping("/delete/{employeeId}")
    public CredentialsModel deleteCredentials(@PathVariable String employeeId) {
        return credentialsService.deleteCredentials(employeeId);
    }
}
