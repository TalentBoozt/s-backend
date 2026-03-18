package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import com.talentboozt.s_backend.domains.edu.model.EWorkspaceMembers;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWorkspaceMembersRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWorkspacesRepository;
import com.talentboozt.s_backend.domains.edu.model.EWorkspaces;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EduWorkspaceMemberService {

    private final EWorkspaceMembersRepository memberRepository;
    private final EWorkspacesRepository workspaceRepository;

    public EduWorkspaceMemberService(EWorkspaceMembersRepository memberRepository, EWorkspacesRepository workspaceRepository) {
        this.memberRepository = memberRepository;
        this.workspaceRepository = workspaceRepository;
    }

    public EWorkspaceMembers addMember(String workspaceId, String userId, ERoles role, String inviterId) {
        EWorkspaces ws = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace missing"));

        if (ws.getTotalMembers() >= ws.getMaxMembers()) {
             throw new RuntimeException("Workspace member limit reached.");
        }

        // Prevent duplicates
        boolean exists = memberRepository.findByWorkspaceIdAndUserId(workspaceId, userId).isPresent();
        if (exists) {
            throw new RuntimeException("User is already in this workspace");
        }

        EWorkspaceMembers member = EWorkspaceMembers.builder()
                .workspaceId(workspaceId)
                .userId(userId)
                .role(role != null ? role : ERoles.LEARNER)
                .status("ACTIVE")
                .invitedBy(inviterId)
                .joinedAt(Instant.now())
                .createdBy(inviterId)
                .build();
                
        ws.setTotalMembers(ws.getTotalMembers() + 1);
        workspaceRepository.save(ws);

        return memberRepository.save(member);
    }
    
    // Simulating quick Bulk Import
    public void bulkImportMembers(String workspaceId, List<String> userIds, String inviterId) {
        // Validation missing for MVP speed up mapping limits
        userIds.forEach(uid -> {
            try {
                addMember(workspaceId, uid, ERoles.LEARNER, inviterId);
            } catch(Exception ignored) { }
        });
    }

    public void removeMember(String workspaceId, String userId) {
        memberRepository.findByWorkspaceIdAndUserId(workspaceId, userId)
            .ifPresent(m -> {
                memberRepository.delete(m);
                EWorkspaces ws = workspaceRepository.findById(workspaceId).orElse(null);
                if (ws != null) {
                    ws.setTotalMembers(Math.max(0, ws.getTotalMembers() - 1));
                    workspaceRepository.save(ws);
                }
            });
    }

    public List<EWorkspaceMembers> getMembers(String workspaceId) {
        return memberRepository.findByWorkspaceId(workspaceId);
    }
}
