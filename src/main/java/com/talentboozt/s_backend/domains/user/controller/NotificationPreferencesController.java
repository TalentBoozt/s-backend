package com.talentboozt.s_backend.domains.user.controller;

import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.user.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/emp_notification")
public class NotificationPreferencesController {

    private final EmployeeService employeeService;

    public NotificationPreferencesController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Update notification preferences for an employee
     */
    @PutMapping("/update/{empId}")
    public ResponseEntity<EmployeeModel> updateNotificationPreferences(
            @PathVariable String empId,
            @RequestBody Map<String, Object> preferences) {
        EmployeeModel employee = employeeService.getEmployee(empId);

        if (employee == null) {
            return ResponseEntity.notFound().build();
        }

        // Update account notifications
        if (preferences.containsKey("accountNotifications")) {
            employee.setAccountNotifications(preferences.get("accountNotifications"));
        }

        // Update marketing notifications
        if (preferences.containsKey("marketingNotifications")) {
            employee.setMarketingNotifications(preferences.get("marketingNotifications"));
        }

        EmployeeModel updated = employeeService.updateEmployee(employee);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get notification preferences for an employee
     */
    @GetMapping("/get/{empId}")
    public ResponseEntity<Map<String, Object>> getNotificationPreferences(@PathVariable String empId) {
        EmployeeModel employee = employeeService.getEmployee(empId);

        if (employee == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> preferences = Map.of(
                "accountNotifications",
                employee.getAccountNotifications() != null ? employee.getAccountNotifications() : Map.of(),
                "marketingNotifications",
                employee.getMarketingNotifications() != null ? employee.getMarketingNotifications() : Map.of());

        return ResponseEntity.ok(preferences);
    }
}
