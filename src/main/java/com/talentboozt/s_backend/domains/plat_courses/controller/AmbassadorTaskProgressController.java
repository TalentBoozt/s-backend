package com.talentboozt.s_backend.domains.plat_courses.controller;

import com.talentboozt.s_backend.domains.plat_courses.model.AmbassadorTaskProgressModel;
import com.talentboozt.s_backend.domains.plat_courses.service.AmbassadorTaskProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v2/ambassador/task-progress")
public class AmbassadorTaskProgressController {

    @Autowired
    AmbassadorTaskProgressService ambassadorTaskProgressService;

    @PostMapping("/add")
    public AmbassadorTaskProgressModel addTaskProgress(@RequestBody AmbassadorTaskProgressModel taskProgress) {
        return ambassadorTaskProgressService.addTaskProgress(taskProgress);
    }

    @GetMapping("/get/all")
    public Iterable<AmbassadorTaskProgressModel> getAllTaskProgress() {
        return ambassadorTaskProgressService.getAllTaskProgress();
    }

    @GetMapping("/get/{id}")
    public AmbassadorTaskProgressModel getTaskProgressById(@PathVariable String id) {
        return ambassadorTaskProgressService.getTaskProgressById(id);
    }

    @PutMapping("/{progressId}/reward")
    public AmbassadorTaskProgressModel rewardProgress(@PathVariable String progressId) {
        return ambassadorTaskProgressService.rewardProgress(progressId);
    }
}
