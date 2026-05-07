package com.talentboozt.s_backend.domains.workspace.service;

import com.talentboozt.s_backend.domains.workspace.model.WorkspaceModel;
import com.talentboozt.s_backend.domains.workspace.repository.WorkspaceRepository;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final CredentialsService credentialsService;

    public WorkspaceService(WorkspaceRepository workspaceRepository, CredentialsService credentialsService) {
        this.workspaceRepository = workspaceRepository;
        this.credentialsService = credentialsService;
    }

    public WorkspaceModel createWorkspace(String name, String ownerId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Workspace name cannot be empty");
        }
        
        String slug = name.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-");
        
        if (workspaceRepository.existsByName(name) || workspaceRepository.existsBySlug(slug)) {
            throw new IllegalArgumentException("Workspace with this name or slug already exists");
        }

        WorkspaceModel workspace = new WorkspaceModel();
        workspace.setName(name);
        workspace.setSlug(slug);
        workspace.setOwnerId(ownerId);
        workspace.setMemberIds(new ArrayList<>(List.of(ownerId)));
        workspace.setCreatedAt(LocalDateTime.now());
        workspace.setUpdatedAt(LocalDateTime.now());
        workspace.setSubscriptionType("FREE");
        
        WorkspaceModel saved = workspaceRepository.save(workspace);
        
        // Update user's credentials with the new organization
        credentialsService.addOrganizationToUser(ownerId, saved.getId(), saved.getName());
        
        return saved;
    }

    public List<WorkspaceModel> getUserWorkspaces(String userId) {
        return workspaceRepository.findByMemberIdsContaining(userId);
    }

    public List<WorkspaceModel> getAllWorkspaces() {
        return workspaceRepository.findAll();
    }

    public Optional<WorkspaceModel> getWorkspaceById(String id) {
        return workspaceRepository.findById(id);
    }

    public WorkspaceModel updateWorkspace(WorkspaceModel workspace) {
        workspace.setUpdatedAt(LocalDateTime.now());
        return workspaceRepository.save(workspace);
    }

    public void deleteWorkspace(String id) {
        workspaceRepository.deleteById(id);
    }
}
