package com.talentboozt.s_backend.domains._private.service;

import com.talentboozt.s_backend.domains._private.model.WhitelistDomains;
import com.talentboozt.s_backend.domains._private.repository.WhitelistDomainsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WhitelistDomainsService {

    @Autowired
    private WhitelistDomainsRepository whitelistDomainsRepository;

    public WhitelistDomains addWhitelistDomain(WhitelistDomains domain) {
        return whitelistDomainsRepository.save(domain);
    }

    public WhitelistDomains getWhitelistDomain(String domain) {
        Optional<WhitelistDomains> domainOptional = whitelistDomainsRepository.findByDomain(domain);
        return domainOptional.orElse(null);
    }

    public void deleteWhitelistDomain(String id) {
        whitelistDomainsRepository.deleteById(id);
    }

    public List<WhitelistDomains> getAllWhitelistDomains() { return whitelistDomainsRepository.findAll(); }

    public List<WhitelistDomains> getWhitelistDomainsByActive(boolean active) { return whitelistDomainsRepository.findByActive(active); }

    public WhitelistDomains getByDomainAndActive(String domain, boolean active) {
        Optional<WhitelistDomains> domainOptional = whitelistDomainsRepository.findByDomainAndActive(domain, active);
        return domainOptional.orElse(null);
    }

    public List<WhitelistDomains> getByRequestBy(String requestedBy) { return whitelistDomainsRepository.findByRequestBy(requestedBy); }

    public WhitelistDomains updateWhitelistDomain(WhitelistDomains domain) {
        Optional<WhitelistDomains> domainOptional = whitelistDomainsRepository.findById(domain.getId());
        if (domainOptional.isPresent()) {
            WhitelistDomains existingDomain = domainOptional.get();
            existingDomain.setDomain(domain.getDomain());
            existingDomain.setRequestBy(domain.getRequestBy());
            return whitelistDomainsRepository.save(existingDomain);
        }
        return null;
    }

    public WhitelistDomains changeActive(String id) {
        Optional<WhitelistDomains> domainOptional = whitelistDomainsRepository.findById(id);
        if (domainOptional.isPresent()) {
            WhitelistDomains existingDomain = domainOptional.get();
            existingDomain.setActive(!existingDomain.isActive());
            return whitelistDomainsRepository.save(existingDomain);
        }
        return null;
    }

    public List<String> getWhitelistDomains() {
        return whitelistDomainsRepository.findByActive(true).stream().map(WhitelistDomains::getDomain).toList();
    }
}
