package com.talentboozt.s_backend.domains.user.controller;

import com.talentboozt.s_backend.domains.user.model.TrainerProfile;
import com.talentboozt.s_backend.domains.user.service.TrainerProfileService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/trainers")
public class TrainerProfileController {
    private final TrainerProfileService trainerProfileService;

    public TrainerProfileController(TrainerProfileService trainerProfileService) {
        this.trainerProfileService = trainerProfileService;
    }

    @GetMapping("/{employeeId}")
    public TrainerProfile getByEmpId(@PathVariable String employeeId) {
        return trainerProfileService.getByEmpId(employeeId);
    }

    @PutMapping("update/empId/{employeeId}")
    public TrainerProfile updateByEmployeeId(@PathVariable String employeeId, @RequestBody TrainerProfile trainerProfile) {
        return trainerProfileService.updateByEmployeeId(employeeId, trainerProfile);
    }
}
