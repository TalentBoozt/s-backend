package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import com.talentboozt.s_backend.domains.edu.exception.EduAccessDeniedException;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduLimitExceededException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.EWorkspaceMembers;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWorkspaceMembersRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWorkspacesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import com.talentboozt.s_backend.domains.edu.model.EWorkspaces;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.dto.workspace.WorkspaceMemberDTO;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EduWorkspaceMemberService {

    private final EWorkspaceMembersRepository memberRepository;
    private final EWorkspacesRepository workspaceRepository;
    private final EUserRepository userRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.talentboozt.s_backend.domains.auth.service.CredentialsService credentialsService;

    public EduWorkspaceMemberService(EWorkspaceMembersRepository memberRepository, 
                                     EWorkspacesRepository workspaceRepository,
                                     EUserRepository userRepository) {
        this.memberRepository = memberRepository;
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
    }

    public WorkspaceMemberDTO addMember(String workspaceId, String userId, ERoles role, String inviterId) {
        EWorkspaces ws = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EduResourceNotFoundException("Workspace missing with id: " + workspaceId));

        // Enforce: only workspace owner or an admin member can add new members
        if (!ws.getOwnerId().equals(inviterId)) {
            boolean isAdmin = memberRepository.findByWorkspaceIdAndUserId(workspaceId, inviterId)
                    .map(m -> m.getRole() == ERoles.ENTERPRISE_ADMIN || m.getRole() == ERoles.PLATFORM_ADMIN)
                    .orElse(false);
            if (!isAdmin) {
                throw new EduAccessDeniedException("Only workspace owner or admin can add members.");
            }
        }

        if (ws.getTotalMembers() >= ws.getMaxMembers()) {
             throw new EduLimitExceededException("Workspace member limit reached. Max allowed: " + ws.getMaxMembers());
        }

        // Prevent duplicates
        boolean exists = memberRepository.findByWorkspaceIdAndUserId(workspaceId, userId).isPresent();
        if (exists) {
            throw new EduBadRequestException("User " + userId + " is already in this workspace");
        }

        ERoles memberRole = role != null ? role : ERoles.ENTERPRISE_LEARNER;
        EWorkspaceMembers member = EWorkspaceMembers.builder()
                .workspaceId(workspaceId)
                .userId(userId)
                .role(memberRole)
                .status("ACTIVE")
                .invitedBy(inviterId)
                .joinedAt(Instant.now())
                .createdBy(inviterId)
                .build();
                
        ws.setTotalMembers(ws.getTotalMembers() + 1);
        workspaceRepository.save(ws);

        // Sync roles and plan on EUser and CredentialsModel
        userRepository.findById(userId).ifPresent(user -> {
            java.util.Set<ERoles> rolesSet = new java.util.HashSet<>();
            if (user.getRoles() != null) {
                rolesSet.addAll(java.util.Arrays.asList(user.getRoles()));
            }
            rolesSet.add(memberRole);
            
            // If the workspace is ENTERPRISE, promote memberRole and set plan
            if (ws.getPlan() == com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan.ENTERPRISE) {
                user.setPlan(com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan.ENTERPRISE);
                if (memberRole == ERoles.ENTERPRISE_INSTRUCTOR) {
                    rolesSet.add(ERoles.ENTERPRISE_INSTRUCTOR);
                } else if (memberRole == ERoles.ENTERPRISE_ADMIN) {
                    rolesSet.add(ERoles.ENTERPRISE_ADMIN);
                }
            } else if (user.getPlan() == null || user.getPlan() == com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan.FREE) {
                user.setPlan(ws.getPlan());
            }

            user.setRoles(rolesSet.toArray(new ERoles[0]));
            userRepository.save(user);

            // Sync globally
            try {
                credentialsService.updateUserRoles(userId, rolesSet.stream().map(Enum::name).collect(java.util.stream.Collectors.toList()));
            } catch (Exception e) {
                // Ignore/log
            }
        });

        EWorkspaceMembers saved = memberRepository.save(member);
        return mapToDTO(saved);
    }

    private WorkspaceMemberDTO mapToDTO(EWorkspaceMembers m) {
        EUser u = userRepository.findById(m.getUserId()).orElse(null);
        return WorkspaceMemberDTO.builder()
                .id(m.getId())
                .workspaceId(m.getWorkspaceId())
                .userId(m.getUserId())
                .userName(u != null ? u.getDisplayName() : "Unknown User")
                .userEmail(u != null ? u.getEmail() : "No Email")
                .userAvatar(u != null ? u.getAvatarUrl() : null)
                .role(m.getRole())
                .status(m.getStatus())
                .joinedAt(m.getJoinedAt())
                .build();
    }
    
    // Simulating quick Bulk Import
    public void bulkImportMembers(String workspaceId, List<String> userIds, String inviterId) {
        // Validation missing for MVP speed up mapping limits
        userIds.forEach(uid -> {
            try {
                addMember(workspaceId, uid, ERoles.ENTERPRISE_LEARNER, inviterId);
            } catch(Exception ignored) { }
        });
    }

    public void removeMember(String workspaceId, String userId) {
        // Cannot remove the workspace owner
        EWorkspaces ws = workspaceRepository.findById(workspaceId).orElse(null);
        if (ws != null && ws.getOwnerId().equals(userId)) {
            throw new EduBadRequestException("Cannot remove workspace owner.");
        }

        memberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
            .ifPresent(m -> {
                memberRepository.delete(m);
                EWorkspaces wspace = workspaceRepository.findById(workspaceId).orElse(null);
                if (wspace != null) {
                    wspace.setTotalMembers(Math.max(0, wspace.getTotalMembers() - 1));
                    workspaceRepository.save(wspace);
                }
            });
    }

    public List<WorkspaceMemberDTO> getMembers(String workspaceId) {
        return memberRepository.findByWorkspaceId(workspaceId).stream()
                .map(this::mapToDTO)
                .toList();
    }
}
