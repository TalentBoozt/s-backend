package com.talentboozt.s_backend.domains.edu.controller;

import jakarta.validation.Valid;
import com.talentboozt.s_backend.domains.edu.dto.workspace.LearningPathRequest;
import com.talentboozt.s_backend.domains.edu.dto.workspace.WorkspaceMemberDTO;
import com.talentboozt.s_backend.domains.edu.dto.workspace.WorkspaceRequest;
import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import com.talentboozt.s_backend.domains.edu.model.ELearningPaths;
import com.talentboozt.s_backend.domains.edu.model.EWorkspaces;
import com.talentboozt.s_backend.domains.edu.service.EduLearningPathService;
import com.talentboozt.s_backend.domains.edu.service.EduWorkspaceMemberService;
import com.talentboozt.s_backend.domains.edu.service.EduWorkspaceService;
import com.talentboozt.s_backend.domains.edu.service.EduWorkspaceGuardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/workspaces")
public class EduWorkspaceController {

    private final EduWorkspaceService workspaceService;
    private final EduWorkspaceMemberService memberService;
    private final EduLearningPathService pathService;
    private final EduWorkspaceGuardService guardService;

    public EduWorkspaceController(EduWorkspaceService workspaceService,
                                  EduWorkspaceMemberService memberService,
                                  EduLearningPathService pathService,
                                  EduWorkspaceGuardService guardService) {
        this.workspaceService = workspaceService;
        this.memberService = memberService;
        this.pathService = pathService;
        this.guardService = guardService;
    }

    // Workspaces 

    @PostMapping("/owner/{ownerId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('CREATOR')")
    public ResponseEntity<EWorkspaces> createWorkspace(
            @PathVariable String ownerId,
            @Valid @RequestBody WorkspaceRequest request) {
        return ResponseEntity.ok(workspaceService.createWorkspace(ownerId, request));
    }

    @GetMapping("/{workspaceId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR') or hasAuthority('ADMIN')")
    public ResponseEntity<EWorkspaces> getWorkspace(@PathVariable String workspaceId,
                                                    @RequestParam String userId) {
        guardService.enforceMembership(workspaceId, userId);
        return ResponseEntity.ok(workspaceService.getWorkspaceById(workspaceId));
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasAuthority('CREATOR') or hasAuthority('ADMIN')")
    public ResponseEntity<List<EWorkspaces>> getWorkspacesByOwner(@PathVariable String ownerId) {
        return ResponseEntity.ok(workspaceService.getWorkspacesByOwner(ownerId));
    }

    // Members

    @PostMapping("/{workspaceId}/members/{userId}")
    @PreAuthorize("hasAuthority('CREATOR') or hasAuthority('ADMIN')")
    public ResponseEntity<WorkspaceMemberDTO> addMember(
            @PathVariable String workspaceId,
            @PathVariable String userId,
            @RequestParam(required = false) ERoles role,
            @RequestParam String inviterId) {
        return ResponseEntity.ok(memberService.addMember(workspaceId, userId, role, inviterId));
    }

    @PostMapping("/{workspaceId}/members/bulk")
    @PreAuthorize("hasAuthority('CREATOR') or hasAuthority('ADMIN')")
    public ResponseEntity<Void> bulkImportMembers(
            @PathVariable String workspaceId,
            @Valid @RequestBody List<String> userIds,
            @RequestParam String inviterId) {
        memberService.bulkImportMembers(workspaceId, userIds, inviterId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{workspaceId}/members")
    @PreAuthorize("hasAuthority('CREATOR') or hasAuthority('ADMIN')")
    public ResponseEntity<List<WorkspaceMemberDTO>> getMembers(@PathVariable String workspaceId,
                                                               @RequestParam String userId) {
        guardService.enforceMembership(workspaceId, userId);
        return ResponseEntity.ok(memberService.getMembers(workspaceId));
    }

    @DeleteMapping("/{workspaceId}/members/{userId}")
    @PreAuthorize("hasAuthority('CREATOR') or hasAuthority('ADMIN')")
    public ResponseEntity<Void> removeMember(
            @PathVariable String workspaceId,
            @PathVariable String userId) {
        memberService.removeMember(workspaceId, userId);
        return ResponseEntity.ok().build();
    }

    // Learning Paths

    @PostMapping("/{workspaceId}/paths")
    @PreAuthorize("hasAuthority('CREATOR') or hasAuthority('ADMIN')")
    public ResponseEntity<ELearningPaths> createPath(
            @PathVariable String workspaceId,
            @RequestParam String creatorId,
            @Valid @RequestBody LearningPathRequest request) {
        return ResponseEntity.ok(pathService.createPath(workspaceId, creatorId, request));
    }

    @GetMapping("/{workspaceId}/paths")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR') or hasAuthority('ADMIN')")
    public ResponseEntity<List<ELearningPaths>> getWorkspacePaths(@PathVariable String workspaceId,
                                                                  @RequestParam String userId) {
        guardService.enforceMembership(workspaceId, userId);
        return ResponseEntity.ok(pathService.getWorkspacePaths(workspaceId));
    }
}
