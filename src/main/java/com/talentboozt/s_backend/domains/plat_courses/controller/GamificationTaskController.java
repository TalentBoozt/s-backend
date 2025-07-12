package com.talentboozt.s_backend.domains.plat_courses.controller;

import com.talentboozt.s_backend.domains.plat_courses.model.GamificationTaskModel;
import com.talentboozt.s_backend.domains.plat_courses.service.GamificationTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/ambassador/gamification")
public class GamificationTaskController {

    @Autowired
    private GamificationTaskService gamificationTaskService;

    @PostMapping("/add")
    public GamificationTaskModel addTask(@RequestBody GamificationTaskModel task) {
        return gamificationTaskService.addTask(task);
    }

    @GetMapping("/get/all")
    public Iterable<GamificationTaskModel> getAllTasks() {
        return gamificationTaskService.getAllTasks();
    }

    @GetMapping("/get/{id}")
    public GamificationTaskModel getTaskById(@PathVariable String id) {
        return gamificationTaskService.getTaskById(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id) {
        gamificationTaskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update")
    public GamificationTaskModel updateTask(@RequestBody GamificationTaskModel task) {
        return gamificationTaskService.updateTask(task);
    }
}
