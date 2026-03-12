package com.talentboozt.s_backend.domains.lifeplanner.planner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentboozt.s_backend.domains.lifeplanner.planner.service.ScheduleRepairService;
import com.talentboozt.s_backend.domains.lifeplanner.ai.model.OptimizedScheduleResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lifeplanner/planner/repair")
@RequiredArgsConstructor
public class RepairController {

    private final ScheduleRepairService scheduleRepairService;

    @PostMapping("/{planId}")
    public ResponseEntity<OptimizedScheduleResponse> repairSchedule(@PathVariable String planId, @RequestHeader("x-user-id") String userId) {
        OptimizedScheduleResponse response = scheduleRepairService.repairSchedule(planId, userId);
        return ResponseEntity.ok(response);
    }
}
