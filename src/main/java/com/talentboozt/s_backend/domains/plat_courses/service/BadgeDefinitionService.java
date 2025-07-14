package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.plat_courses.model.BadgeDefinition;
import com.talentboozt.s_backend.domains.plat_courses.repository.BadgeDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BadgeDefinitionService {

    @Autowired
    private BadgeDefinitionRepository badgeRepo;

    public BadgeDefinition addBadge(BadgeDefinition badge) {
        return badgeRepo.save(badge);
    }

    public BadgeDefinition getBadge(String id) { return badgeRepo.findById(id).orElse(null); }

    public void deleteBadge(String id) { badgeRepo.deleteById(id); }

    public Iterable<BadgeDefinition> getAllBadges() { return badgeRepo.findAll(); }

    public BadgeDefinition updateBadge(BadgeDefinition badge) {
        Optional<BadgeDefinition> existingBadge = badgeRepo.findById(badge.getId());
        if (existingBadge.isPresent()) {
            return badgeRepo.save(badge);
        }
        return null;
    }

    public BadgeDefinition changeVisibility(String id) {
        Optional<BadgeDefinition> badge = badgeRepo.findById(id);
        if (badge.isPresent()) {
            BadgeDefinition badgeDefinition = badge.get();
            badgeDefinition.setVisible(!badgeDefinition.isVisible());
            return badgeRepo.save(badgeDefinition);
        }
        return null;
    }
}
