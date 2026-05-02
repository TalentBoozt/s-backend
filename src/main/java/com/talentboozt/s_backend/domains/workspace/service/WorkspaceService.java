package com.talentboozt.s_backend.domains.workspace.service;

import com.talentboozt.s_backend.domains.workspace.model.WorkspaceModel;
import com.talentboozt.s_backend.domains.workspace.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public WorkspaceModel createWorkspace(String name, String ownerId) {
        WorkspaceModel workspace = new WorkspaceModel();
        workspace.setName(name);
        workspace.setSlug(name.toLowerCase().trim().replaceAll("[^a-z0-9]+", "-"));
        workspace.setOwnerId(ownerId);
        workspace.setMemberIds(new ArrayList<>(List.of(ownerId)));
        workspace.setCreatedAt(LocalDateTime.now());
        workspace.setUpdatedAt(LocalDateTime.now());
        workspace.setSubscriptionType("FREE");
        return workspaceRepository.save(workspace);
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
