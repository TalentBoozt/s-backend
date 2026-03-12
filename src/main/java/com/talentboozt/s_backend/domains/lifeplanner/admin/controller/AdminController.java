package com.talentboozt.s_backend.domains.lifeplanner.admin.controller;

import com.talentboozt.s_backend.domains.lifeplanner.admin.service.LifePlannerAdminService;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.user.model.PlatformRole;
import com.talentboozt.s_backend.domains.user.repository.mongodb.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/lifeplanner/admin")
@RequiredArgsConstructor
public class AdminController {

    private final LifePlannerAdminService adminService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestHeader("x-user-id") String userId) {
        // Enforce PLATFORM_ADMIN check
        EmployeeModel employee = employeeRepository.findById(userId).orElse(null);
        if (employee == null || employee.getPlatformRole() != PlatformRole.PLATFORM_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied. Admin role required."));
        }

        return ResponseEntity.ok(adminService.getPlatformStats());
    }
}
