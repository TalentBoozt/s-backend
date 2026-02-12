package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorPointAudit;
import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.repository.mongodb.AmbassadorPointAuditRepository;
import com.talentboozt.s_backend.domains.ambassador.repository.mongodb.AmbassadorProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class AmbassadorPointService {

    @Autowired
    private AmbassadorProfileRepository ambassadorRepo;

    @Autowired
    private AmbassadorPointAuditRepository auditRepo;

    public void handleDailyLogin(String userId) {
        Optional<AmbassadorProfileModel> optional = ambassadorRepo.findByEmployeeId(userId);
        if (optional.isEmpty()) return;

        AmbassadorProfileModel ambassador = optional.get();

        int pointsToday = 1;
        boolean bonusGiven = false;

        LocalDate today = LocalDate.now();
        LocalDate lastLogin = ambassador.getLastLoginDate() != null
                ? ambassador.getLastLoginDate().atZone(ZoneId.systemDefault()).toLocalDate()
                : null;

        if (lastLogin != null && ChronoUnit.DAYS.between(lastLogin, today) == 1) {
            ambassador.setConsecutiveLoginDays(ambassador.getConsecutiveLoginDays() + 1);
        } else if (lastLogin == null || ChronoUnit.DAYS.between(lastLogin, today) > 1) {
            ambassador.setConsecutiveLoginDays(1); // reset streak
        }

        ambassador.setLastLoginDate(today.atStartOfDay().toInstant(ZoneOffset.UTC));
        ambassador.setTotalLogins(ambassador.getTotalLogins() + 1);
        ambassador.setPoints(ambassador.getPoints() + pointsToday);
        ambassador.setLastPointEarnedAt(Instant.now());

        record(userId, "DAILY_LOGIN", pointsToday, null);

        if (ambassador.getConsecutiveLoginDays() == 25) {
            ambassador.setPoints(ambassador.getPoints() + 10);
            bonusGiven = true;
            record(userId, "STREAK_25_DAYS", 10, "{\"streak\":25}");
        }

        ambassadorRepo.save(ambassador);
    }

    public void addPoints(String userId, int points, String reason, String metadata) {
        Optional<AmbassadorProfileModel> optional = ambassadorRepo.findByEmployeeId(userId);
        if (optional.isEmpty()) return;

        AmbassadorProfileModel ambassador = optional.get();
        ambassador.setPoints(ambassador.getPoints() + points);
        ambassador.setLastPointEarnedAt(Instant.now());
        ambassadorRepo.save(ambassador);

        record(userId, reason, points, metadata);
    }

    private void record(String userId, String reason, int points, String metadata) {
        AmbassadorPointAudit audit = new AmbassadorPointAudit();
        audit.setAmbassadorId(userId);
        audit.setReason(reason);
        audit.setPoints(points);
        audit.setCreatedAt(Instant.now());
        audit.setMetadata(metadata);

        auditRepo.save(audit);
    }
}
