package com.talentboozt.s_backend.domains._private.controller;

import com.talentboozt.s_backend.domains._private.model.WhitelistDomains;
import com.talentboozt.s_backend.domains._private.service.WhitelistDomainsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/v2/redirect")
public class RedirectController {

    @Autowired
    WhitelistDomainsService whitelistDomainsService;

    @PostMapping("/validate-url")
    public ResponseEntity<Boolean> validateUrl(@RequestBody WhitelistDomains whitelistDomains) {
        String redirectUrl = whitelistDomains.getDomain();
        List<String> allowedDomains = whitelistDomainsService.getWhitelistDomains();

        if (!allowedDomains.isEmpty() && isValidUrl(redirectUrl)) {
            try {
                URI uri = new URI(redirectUrl);
                String host = uri.getHost();
                if (allowedDomains.contains(host)) {
                    return ResponseEntity.ok(true);
                }
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
            } catch (URISyntaxException e) {
                return ResponseEntity.badRequest().body(false);
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
        }
    }

    private boolean isValidUrl(String url) {
        try {
            URI uri = new URI(url);
            return "https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
