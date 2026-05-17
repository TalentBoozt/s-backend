package com.talentboozt.s_backend.domains.organization.repository;

import com.talentboozt.s_backend.domains.organization.model.OrganizationModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends MongoRepository<OrganizationModel, String> {
    Optional<OrganizationModel> findBySlug(String slug);
    Optional<OrganizationModel> findByName(String name);
    boolean existsBySlug(String slug);
}
