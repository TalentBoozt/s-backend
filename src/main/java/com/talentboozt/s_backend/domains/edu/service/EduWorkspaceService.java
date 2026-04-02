package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.workspace.WorkspaceRequest;
import com.talentboozt.s_backend.domains.edu.model.EWorkspaces;
import com.talentboozt.s_backend.domains.edu.model.EWorkspaceMembers;
import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWorkspacesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWorkspaceMembersRepository;
import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.edu.dto.EWSettingsDTO;
import com.talentboozt.s_backend.domains.edu.dto.EWProfileDTO;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;

import java.time.Instant;
import java.util.List;

@Service
public class EduWorkspaceService {

    private final EWorkspacesRepository workspaceRepository;
    private final EWorkspaceMembersRepository memberRepository;

    public EduWorkspaceService(EWorkspacesRepository workspaceRepository, EWorkspaceMembersRepository memberRepository) {
        this.workspaceRepository = workspaceRepository;
        this.memberRepository = memberRepository;
    }

    public EWorkspaces createWorkspace(String ownerId, WorkspaceRequest request) {
        EWSettingsDTO settings = new EWSettingsDTO();
        settings.setAllowPublicRegistration(false);
        settings.setRequireAdminApproval(true);
        settings.setIsBrandingEnabled(false);
        settings.setDefaultRole(ERoles.ENTERPRISE_LEARNER.name());

        EWProfileDTO profile = new EWProfileDTO(); // Simple mapping since old DTO was broken previously

        EWorkspaces workspace = EWorkspaces.builder()
                .ownerId(ownerId)
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .plan(request.getPlan())
                .domain(request.getDomain())
                .isActive(true)
                .maxMembers(100) // Default scale, override by plan natively
                .totalMembers(1) // Owner counts
                .totalCourses(0)
                .settings(settings)
                .profile(profile)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
                
        EWorkspaces saved = workspaceRepository.save(workspace);

        // Auto-assign owner as Admin member natively
        EWorkspaceMembers ownerMember = EWorkspaceMembers.builder()
                .workspaceId(saved.getId())
                .userId(ownerId)
                .role(ERoles.ENTERPRISE_ADMIN) // Highest Tier
                .status("ACTIVE")
                .joinedAt(Instant.now())
                .lastActiveAt(Instant.now())
                .createdBy("SYSTEM")
                .build();
        memberRepository.save(ownerMember);

        return saved;
    }

    public EWorkspaces getWorkspaceById(String workspaceId) {
        return workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EduResourceNotFoundException("Workspace not found with id: " + workspaceId));
    }

    public List<EWorkspaces> getWorkspacesByOwner(String ownerId) {
        return workspaceRepository.findByOwnerId(ownerId);
    }
}
