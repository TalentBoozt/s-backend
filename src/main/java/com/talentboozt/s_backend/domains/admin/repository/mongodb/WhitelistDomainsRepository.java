package com.talentboozt.s_backend.domains.admin.repository.mongodb;

import com.talentboozt.s_backend.domains.admin.model.WhitelistDomains;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface WhitelistDomainsRepository extends MongoRepository<WhitelistDomains, String> {
    List<WhitelistDomains> findByActive(boolean active);
    Optional<WhitelistDomains> findByDomain(String domain);
    Optional<WhitelistDomains> findByDomainAndActive(String domain, boolean active);
    List<WhitelistDomains> findByRequestBy(String requestBy);
}
