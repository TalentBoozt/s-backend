package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.models.FinWorkspace;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinWorkspaceRepository;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FinWorkspaceService {
    private final FinWorkspaceRepository workspaceRepository;
    private final CredentialsService credentialsService;

    public FinWorkspace createWorkspace(String name, String ownerId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace name cannot be empty");
        }

        String slug = name.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-");

        if (workspaceRepository.existsByName(name) || workspaceRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("Finance workspace with this name or slug already exists");
        }

        FinWorkspace workspace = FinWorkspace.builder()
                .name(name)
                .slug(slug)
                .ownerId(ownerId)
                .memberIds(new ArrayList<>(List.of(ownerId)))
                .subscriptionType("FREE")
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        FinWorkspace saved = workspaceRepository.save(workspace);
        
        // Link to global credentials
        credentialsService.addOrganizationToUser(ownerId, saved.getId(), saved.getName());
        
        return saved;
    }

    public List<FinWorkspace> getUserWorkspaces(String userId) {
        return workspaceRepository.findByMemberIdsContaining(userId);
    }

    public List<FinWorkspace> getAllWorkspaces() {
        return workspaceRepository.findAll();
    }

    public Optional<FinWorkspace> getWorkspaceById(String id) {
        return workspaceRepository.findById(id);
    }

    public FinWorkspace updateWorkspace(FinWorkspace workspace) {
        workspace.setUpdatedAt(Instant.now());
        return workspaceRepository.save(workspace);
    }

    public void deleteWorkspace(String id) {
        workspaceRepository.deleteById(id);
    }
}
