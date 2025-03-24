package com.talentboozt.s_backend.Controller.common.auth;

import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import com.talentboozt.s_backend.Service.common.CredentialsService;
import com.talentboozt.s_backend.Service.common.JwtService;
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
    public Iterable<CredentialsModel> getAllCredentials(@RequestHeader("Authorization") String token) {
        String extractToken = token.substring(7);
        if (!jwtService.validateToken(extractToken)) return null;
        return credentialsService.getAllCredentials();
    }

    @GetMapping("/get/{id}")
    public Optional<CredentialsModel> getCredentials(@RequestHeader("Authorization") String token, @PathVariable String id) {
        String extractToken = token.substring(7);
        if (!jwtService.validateToken(extractToken)) return Optional.empty();
        return credentialsService.getCredentials(id);
    }

    @GetMapping("/getByEmail/{email}")
    public CredentialsModel getCredentialsByEmail(@RequestHeader("Authorization") String token, @PathVariable String email) {
        String extractToken = token.substring(7);
        if (!jwtService.validateToken(extractToken)) return null;
        return credentialsService.getCredentialsByEmail(email);
    }

    @GetMapping("/getByEmployeeId/{employeeId}")
    public Optional<CredentialsModel> getCredentialsByEmployeeId(@RequestHeader("Authorization") String token, @PathVariable String employeeId) {
        String extractToken = token.substring(7);
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
