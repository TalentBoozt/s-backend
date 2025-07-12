package com.talentboozt.s_backend.domains._private.controller;

import com.talentboozt.s_backend.domains._private.model.WhitelistDomains;
import com.talentboozt.s_backend.domains._private.service.WhitelistDomainsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/whitelist-domains")
public class WhitelistDomainsController {

    @Autowired
    private WhitelistDomainsService whitelistDomainsService;

    @PostMapping("/add")
    public WhitelistDomains addWhitelistDomain(@RequestBody WhitelistDomains domain) {
        return whitelistDomainsService.addWhitelistDomain(domain);
    }

    @PostMapping("/get")
    public WhitelistDomains getWhitelistDomain(@RequestBody WhitelistDomains domain) {
        return whitelistDomainsService.getWhitelistDomain(domain.getDomain());
    }

    @DeleteMapping("/delete/{id}")
    public void deleteWhitelistDomain(@PathVariable String id) {
        whitelistDomainsService.deleteWhitelistDomain(id);
    }

    @GetMapping("/get-all")
    public List<WhitelistDomains> getAllWhitelistDomains() {
        return whitelistDomainsService.getAllWhitelistDomains();
    }

    @GetMapping("/get-active")
    public List<WhitelistDomains> getWhitelistDomainsByActive() {
        return whitelistDomainsService.getWhitelistDomainsByActive(true);
    }

    @PutMapping("/update")
    public WhitelistDomains updateWhitelistDomain(@RequestBody WhitelistDomains domain) {
        return whitelistDomainsService.updateWhitelistDomain(domain);
    }

    @PutMapping("/change-active/{id}")
    public WhitelistDomains changeActive(@PathVariable String id) {
        return whitelistDomainsService.changeActive(id);
    }

    @GetMapping("/get-by-request-by/{requestedBy}")
    public List<WhitelistDomains> getByRequestBy(@PathVariable String requestedBy) {
        return whitelistDomainsService.getByRequestBy(requestedBy);
    }

    @PostMapping("/get-by-domain-and-active")
    public WhitelistDomains getByDomainAndActive(@RequestBody WhitelistDomains domain) {
        return whitelistDomainsService.getByDomainAndActive(domain.getDomain(), true);
    }

    @GetMapping("/get-whitelist")
    public List<String> getWhitelist() {
        return whitelistDomainsService.getWhitelistDomains();
    }
}
