package com.talentboozt.s_backend.domains.finance_planning.controllers;

import com.talentboozt.s_backend.domains.finance_planning.models.FinProjectMember;
import com.talentboozt.s_backend.domains.finance_planning.security.annotations.RequiresFinPermission;
import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;
import com.talentboozt.s_backend.domains.finance_planning.repository.mongodb.FinProjectMemberRepository;
import com.talentboozt.s_backend.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/finance/projects/{projectId}/members")
@RequiredArgsConstructor
public class FinProjectMemberController {
    private final FinProjectMemberRepository memberRepository;

    @GetMapping
    @RequiresFinPermission(FinPermission.READ_PROJECT)
    public ResponseEntity<ApiResponse<List<FinProjectMember>>> getMembers(@PathVariable String projectId) {
        return ResponseEntity.ok(ApiResponse.success(memberRepository.findByProjectId(projectId)));
    }

    @PostMapping
    @RequiresFinPermission(FinPermission.MANAGE_USERS)
    public ResponseEntity<ApiResponse<FinProjectMember>> addMember(
            @PathVariable String projectId,
            @RequestBody FinProjectMember member) {
        member.setProjectId(projectId);
        member.setJoinedAt(Instant.now());
        return ResponseEntity.ok(ApiResponse.success(memberRepository.save(member)));
    }

    @DeleteMapping("/{userId}")
    @RequiresFinPermission(FinPermission.MANAGE_USERS)
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable String projectId,
            @PathVariable String userId) {
        memberRepository.findByProjectIdAndUserId(projectId, userId)
                .ifPresent(memberRepository::delete);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
