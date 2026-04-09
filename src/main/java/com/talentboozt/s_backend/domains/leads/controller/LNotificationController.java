package com.talentboozt.s_backend.domains.leads.controller;

import com.talentboozt.s_backend.domains.leads.model.LNotification;
import com.talentboozt.s_backend.domains.leads.repository.LNotificationRepository;
import com.talentboozt.s_backend.shared.security.utils.SecurityUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leads/notifications")
public class LNotificationController {
    private final LNotificationRepository repository;
    private final SecurityUtils securityUtils;

    public LNotificationController(LNotificationRepository repository, SecurityUtils securityUtils) {
        this.repository = repository;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public List<LNotification> getNotifications() {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        if (workspaceId == null) throw new RuntimeException("No workspace found");
        return repository.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId);
    }

    @GetMapping("/unread/count")
    public long getUnreadCount() {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        if (workspaceId == null) throw new RuntimeException("No workspace found");
        return repository.countByWorkspaceIdAndReadFalse(workspaceId);
    }

    @PutMapping("/{id}/read")
    public LNotification markAsRead(@PathVariable String id) {
        LNotification n = repository.findById(id).orElseThrow();
        n.setRead(true);
        return repository.save(n);
    }

    @DeleteMapping("/clear")
    public void clearAll() {
        String workspaceId = securityUtils.getCurrentWorkspaceId();
        if (workspaceId == null) throw new RuntimeException("No workspace found");
        List<LNotification> all = repository.findByWorkspaceId(workspaceId);
        repository.deleteAll(all);
    }
}
