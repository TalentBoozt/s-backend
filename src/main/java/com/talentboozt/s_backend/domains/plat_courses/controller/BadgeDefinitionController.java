package com.talentboozt.s_backend.domains.plat_courses.controller;

import com.talentboozt.s_backend.domains.plat_courses.model.BadgeDefinition;
import com.talentboozt.s_backend.domains.plat_courses.service.BadgeDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/ambassador/badges")
public class BadgeDefinitionController {

    @Autowired
    BadgeDefinitionService badgeDefinitionService;

    @PostMapping("/add")
    public BadgeDefinition addBadge(@RequestBody BadgeDefinition badge) {
        return badgeDefinitionService.addBadge(badge);
    }

    @PutMapping("/update")
    public BadgeDefinition updateBadge(@RequestBody BadgeDefinition badge) {
        return badgeDefinitionService.updateBadge(badge);
    }

    @PutMapping("/changeVisibility/{id}")
    public BadgeDefinition changeVisibility(@PathVariable String id) {
        return badgeDefinitionService.changeVisibility(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteBadge(@PathVariable String id) {
        badgeDefinitionService.deleteBadge(id);
    }

    @GetMapping("/get/{id}")
    public BadgeDefinition getBadge(@PathVariable String id) {
        return badgeDefinitionService.getBadge(id);
    }

    @GetMapping("/getAll")
    public Iterable<BadgeDefinition> getAllBadges() {
        return badgeDefinitionService.getAllBadges();
    }
}
