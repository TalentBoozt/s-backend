package com.talentboozt.s_backend.domains.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/edu/admin/health")
@RequiredArgsConstructor
public class EduAdminHealthController {

    private final HealthEndpoint healthEndpoint;

    @GetMapping
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        HealthComponent health = healthEndpoint.health();
        
        Map<String, Object> details = new HashMap<>();
        details.put("status", health.getStatus().getCode());
        
        // Use healthEndpoint directly or safely cast if possible, 
        // but for general health, the result of health() is typically enough.
        // If it's a SystemHealth, we can get components.
        if (health instanceof org.springframework.boot.actuate.health.SystemHealth systemHealth) {
            details.put("components", systemHealth.getComponents());
        }
        
        // Add JVM metrics summary
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> jvm = new HashMap<>();
        jvm.put("totalMemoryMB", runtime.totalMemory() / 1024 / 1024);
        jvm.put("freeMemoryMB", runtime.freeMemory() / 1024 / 1024);
        jvm.put("usedMemoryMB", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
        jvm.put("maxMemoryMB", runtime.maxMemory() / 1024 / 1024);
        jvm.put("availableProcessors", runtime.availableProcessors());
        
        details.put("jvm", jvm);
        
        return ResponseEntity.ok(details);
    }
}
