package com.talentboozt.s_backend.domains.software_marketplace.controller;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.domains.software_marketplace.model.SoftwareAppModel;
import com.talentboozt.s_backend.domains.software_marketplace.service.SoftwareAppService;
import com.talentboozt.s_backend.shared.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/marketplace/apps")
public class SoftwareAppController {

    @Autowired
    private SoftwareAppService softwareAppService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CredentialsService credentialsService;

    private String getCompanyId(HttpServletRequest request) {
        String token = jwtService.extractTokenFromHeaderOrCookie(request);
        if (token == null || !jwtService.validateToken(token))
            return null;
        CredentialsModel userTokenInfo = jwtService.getUserFromToken(token);
        Optional<CredentialsModel> fullUser = credentialsService
                .getCredentialsByEmployeeId(userTokenInfo.getEmployeeId());
        return fullUser.map(CredentialsModel::getCompanyId).orElse(null);
    }

    @GetMapping("/purchased")
    public ResponseEntity<List<SoftwareAppModel>> getPurchasedApps(HttpServletRequest request) {
        String companyId = getCompanyId(request);
        if (companyId == null)
            return ResponseEntity.status(401).build();
        return ResponseEntity.ok(softwareAppService.getPurchasedApps(companyId));
    }

    @PostMapping("/add")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('system:manage')")
    public ResponseEntity<SoftwareAppModel> addApp(@RequestBody SoftwareAppModel app, HttpServletRequest request) {
        return ResponseEntity.ok(softwareAppService.saveApp(app));
    }

    @DeleteMapping("/remove/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('system:manage')")
    public ResponseEntity<Void> removeApp(@PathVariable String id) {
        softwareAppService.deleteApp(id);
        return ResponseEntity.ok().build();
    }
}
