package com.talentboozt.s_backend.domains.finance_planning.services;

import com.talentboozt.s_backend.domains.finance_planning.models.FinWorkspace;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinWorkspaceRepository;
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

    public FinWorkspace createWorkspace(String name, String ownerId) {
        FinWorkspace workspace = FinWorkspace.builder()
                .name(name)
                .slug(name.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-"))
                .ownerId(ownerId)
                .memberIds(new ArrayList<>(List.of(ownerId)))
                .subscriptionType("FREE")
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return workspaceRepository.save(workspace);
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
