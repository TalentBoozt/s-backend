package com.talentboozt.s_backend.domains.workspace.controller;

import com.talentboozt.s_backend.domains.workspace.model.WorkspaceModel;
import com.talentboozt.s_backend.domains.workspace.service.WorkspaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping
    public ResponseEntity<WorkspaceModel> createWorkspace(@RequestBody Map<String, String> request, 
                                                         @RequestHeader("x-user-id") String userId) {
        String name = request.get("name");
        return ResponseEntity.ok(workspaceService.createWorkspace(name, userId));
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceModel>> getUserWorkspaces(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(workspaceService.getUserWorkspaces(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceModel> getWorkspaceById(@PathVariable String id) {
        return workspaceService.getWorkspaceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
