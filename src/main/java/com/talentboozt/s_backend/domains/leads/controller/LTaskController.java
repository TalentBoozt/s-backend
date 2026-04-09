package com.talentboozt.s_backend.domains.leads.controller;

import com.talentboozt.s_backend.domains.leads.model.LTask;
import com.talentboozt.s_backend.domains.leads.repository.LTaskRepository;
import com.talentboozt.s_backend.shared.security.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leads/tasks")
public class LTaskController {
    private final LTaskRepository repository;
    private final com.talentboozt.s_backend.domains.leads.service.LTaskService taskService;
    private final SecurityUtils securityUtils;

    public LTaskController(LTaskRepository repository, 
                          com.talentboozt.s_backend.domains.leads.service.LTaskService taskService,
                          SecurityUtils securityUtils) {
        this.repository = repository;
        this.taskService = taskService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public List<LTask> getActiveTasks() {
        String wsId = securityUtils.getCurrentWorkspaceId();
        if (wsId == null) throw new RuntimeException("No workspace found");
        return repository.findByWorkspaceIdAndStatusIn(wsId, List.of("PENDING", "PROCESSING"));
    }

    @GetMapping("/{id}")
    public LTask getTaskStatus(@PathVariable String id) {
        return repository.findById(id).orElseThrow();
    }

    @PostMapping("/ai-template")
    public LTask startAiTemplateTask(@RequestBody java.util.Map<String, Object> body) {
        String wsId = securityUtils.getCurrentWorkspaceId();
        String userId = securityUtils.getCurrentUserId();
        if (wsId == null) throw new RuntimeException("No workspace found");
        
        LTask task = taskService.createAndQueueTask(wsId, userId, "AI_TEMPLATE_GEN", body);
        taskService.executeAiTemplateGeneration(task);
        return task;
    }
}
