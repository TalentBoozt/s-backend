package com.talentboozt.s_backend.domains.organization.service;

import com.talentboozt.s_backend.domains.organization.model.OrganizationModel;
import com.talentboozt.s_backend.domains.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    public OrganizationModel createOrganization(OrganizationModel organization, String ownerId) {
        organization.setOwnerId(ownerId);
        organization.setCreatedAt(LocalDateTime.now());
        organization.setUpdatedAt(LocalDateTime.now());
        organization.setActive(true);
        
        List<OrganizationModel.OrganizationMember> members = new ArrayList<>();
        members.add(OrganizationModel.OrganizationMember.builder()
                .userId(ownerId)
                .role("OWNER")
                .joinedAt(LocalDateTime.now())
                .build());
        organization.setMembers(members);
        
        return organizationRepository.save(organization);
    }

    public OrganizationModel getOrganizationById(String id) {
        return organizationRepository.findById(id).orElse(null);
    }

    public OrganizationModel getOrganizationBySlug(String slug) {
        return organizationRepository.findBySlug(slug).orElse(null);
    }

    public List<OrganizationModel> getAllOrganizations() {
        return organizationRepository.findAll();
    }
}
