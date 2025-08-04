package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorPointAudit;
import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorPointAuditRepository;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmbassadorPointServiceTest {

    @Mock
    private AmbassadorProfileRepository ambassadorRepo;

    @Mock
    private AmbassadorPointAuditRepository auditRepo;

    @InjectMocks
    private AmbassadorPointService ambassadorPointService;

    @Test
    void handleDailyLoginShouldAwardPointsForFirstLogin() {
        AmbassadorProfileModel ambassador = new AmbassadorProfileModel();
        ambassador.setEmployeeId("user123");
        ambassador.setLastLoginDate(null);
        ambassador.setPoints(0);
        ambassador.setTotalLogins(0);

        when(ambassadorRepo.findByEmployeeId("user123")).thenReturn(Optional.of(ambassador));

        ambassadorPointService.handleDailyLogin("user123");

        assertEquals(1, ambassador.getPoints());
        assertEquals(1, ambassador.getTotalLogins());
        verify(auditRepo).save(any(AmbassadorPointAudit.class));
        verify(ambassadorRepo).save(ambassador);
    }

    @Test
    void handleDailyLoginShouldResetStreakIfLoginMissed() {
        AmbassadorProfileModel ambassador = new AmbassadorProfileModel();
        ambassador.setEmployeeId("user123");
        ambassador.setLastLoginDate(Instant.now().minus(3, ChronoUnit.DAYS));
        ambassador.setConsecutiveLoginDays(5);

        when(ambassadorRepo.findByEmployeeId("user123")).thenReturn(Optional.of(ambassador));

        ambassadorPointService.handleDailyLogin("user123");

        assertEquals(1, ambassador.getConsecutiveLoginDays());
        verify(ambassadorRepo).save(ambassador);
    }

    @Test
    void handleDailyLoginShouldAwardBonusFor25DayStreak() {
        AmbassadorProfileModel ambassador = new AmbassadorProfileModel();
        ambassador.setEmployeeId("user123");
        ambassador.setLastLoginDate(Instant.now().minus(1, ChronoUnit.DAYS));
        ambassador.setConsecutiveLoginDays(24);
        ambassador.setPoints(100);

        when(ambassadorRepo.findByEmployeeId("user123")).thenReturn(Optional.of(ambassador));

        ambassadorPointService.handleDailyLogin("user123");

        assertEquals(25, ambassador.getConsecutiveLoginDays());
        assertEquals(111, ambassador.getPoints());
        verify(auditRepo, times(2)).save(any(AmbassadorPointAudit.class));
        verify(ambassadorRepo).save(ambassador);
    }

    @Test
    void handleDailyLoginShouldDoNothingIfUserNotFound() {
        when(ambassadorRepo.findByEmployeeId("user123")).thenReturn(Optional.empty());

        ambassadorPointService.handleDailyLogin("user123");

        verify(ambassadorRepo, never()).save(any());
        verify(auditRepo, never()).save(any());
    }

    @Test
    void addPointsShouldIncreasePointsAndRecordAudit() {
        AmbassadorProfileModel ambassador = new AmbassadorProfileModel();
        ambassador.setEmployeeId("user123");
        ambassador.setPoints(50);

        when(ambassadorRepo.findByEmployeeId("user123")).thenReturn(Optional.of(ambassador));

        ambassadorPointService.addPoints("user123", 20, "BONUS", "{\"reason\":\"special\"}");

        assertEquals(70, ambassador.getPoints());
        verify(auditRepo).save(any(AmbassadorPointAudit.class));
        verify(ambassadorRepo).save(ambassador);
    }

    @Test
    void addPointsShouldDoNothingIfUserNotFound() {
        when(ambassadorRepo.findByEmployeeId("user123")).thenReturn(Optional.empty());

        ambassadorPointService.addPoints("user123", 20, "BONUS", "{\"reason\":\"special\"}");

        verify(ambassadorRepo, never()).save(any());
        verify(auditRepo, never()).save(any());
    }
}
