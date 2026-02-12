package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.plat_courses.model.BadgeDefinition;
import com.talentboozt.s_backend.domains.plat_courses.repository.mongodb.BadgeDefinitionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class BadgeDefinitionService {

    @Autowired
    private BadgeDefinitionRepository badgeRepo;

    public BadgeDefinition addBadge(BadgeDefinition badge) {
        return badgeRepo.save(Objects.requireNonNull(badge));
    }

    public BadgeDefinition getBadge(String id) { return badgeRepo.findById(Objects.requireNonNull(id)).orElse(null); }

    public void deleteBadge(String id) { badgeRepo.deleteById(Objects.requireNonNull(id)); }

    public Iterable<BadgeDefinition> getAllBadges() { return badgeRepo.findAll(); }

    public BadgeDefinition updateBadge(BadgeDefinition badge) {
        Optional<BadgeDefinition> existingBadge = badgeRepo.findById(Objects.requireNonNull(badge.getId()));
        if (existingBadge.isPresent()) {
            return badgeRepo.save(badge);
        }
        return null;
    }

    public BadgeDefinition changeVisibility(String id) {
        Optional<BadgeDefinition> badge = badgeRepo.findById(Objects.requireNonNull(id));
        if (badge.isPresent()) {
            BadgeDefinition badgeDefinition = badge.get();
            badgeDefinition.setVisible(!badgeDefinition.isVisible());
            return badgeRepo.save(badgeDefinition);
        }
        return null;
    }
}
