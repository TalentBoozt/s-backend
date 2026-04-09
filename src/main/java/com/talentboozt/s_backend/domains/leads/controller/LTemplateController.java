package com.talentboozt.s_backend.domains.leads.controller;

import com.talentboozt.s_backend.domains.leads.model.LTemplate;
import com.talentboozt.s_backend.domains.leads.repository.LTemplateRepository;
import com.talentboozt.s_backend.shared.security.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leads/templates")
public class LTemplateController {
    private final LTemplateRepository repository;
    private final SecurityUtils securityUtils;

    public LTemplateController(LTemplateRepository repository, SecurityUtils securityUtils) {
        this.repository = repository;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public List<LTemplate> getTemplates() {
        String wsId = securityUtils.getCurrentWorkspaceId();
        if (wsId == null) throw new RuntimeException("No workspace found");
        return repository.findByWorkspaceIdOrderByUsageCountDesc(wsId);
    }

    @PostMapping
    public LTemplate saveTemplate(@RequestBody LTemplate template) {
        String wsId = securityUtils.getCurrentWorkspaceId();
        if (wsId == null) throw new RuntimeException("No workspace found");
        template.setWorkspaceId(wsId);
        return repository.save(template);
    }

    @DeleteMapping("/{id}")
    public void deleteTemplate(@PathVariable String id) {
        repository.deleteById(id);
    }
}
