package com.talentboozt.s_backend.domains.audit_logs.service;

import com.stripe.model.Event;
import com.talentboozt.s_backend.domains.audit_logs.model.StripeAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.repository.mongodb.StripeAuditLogRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StripeAuditLogServiceTest {

    @Mock
    private StripeAuditLogRepository auditLogRepository;

    @InjectMocks
    private StripeAuditLogService stripeAuditLogService;

    @Test
    void logEventSavesAuditLog() {
        // Arrange
        Event event = mock(Event.class);
        when(event.getId()).thenReturn("evt_123");
        when(event.getType()).thenReturn("payment_intent.created");

        String rawPayload = "{\"data\": \"sample payload\"}";

        StripeAuditLog expectedLog = new StripeAuditLog();
        expectedLog.setEventId("evt_123");
        expectedLog.setEventType("payment_intent.created");
        expectedLog.setRawPayload(rawPayload);
        expectedLog.setStatus("pending");
        expectedLog.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS)); // default expiration in the service

        when(auditLogRepository.save(any(StripeAuditLog.class))).thenReturn(expectedLog);

        // Act
        stripeAuditLogService.logEvent(event, rawPayload);

        // Assert
        verify(auditLogRepository).save(argThat(log ->
                log.getEventId().equals("evt_123") &&
                        log.getEventType().equals("payment_intent.created") &&
                        log.getRawPayload().equals(rawPayload) &&
                        log.getStatus().equals("pending")
        ));
    }

    @Test
    void markProcessedUpdatesStatus() {
        // Arrange
        String eventId = "evt_123";
        StripeAuditLog log = new StripeAuditLog();
        log.setEventId(eventId);
        log.setStatus("pending");
        when(auditLogRepository.findById(eventId)).thenReturn(Optional.of(log));

        // Act
        stripeAuditLogService.markProcessed(eventId);

        // Assert
        verify(auditLogRepository).save(argThat(updatedLog ->
                updatedLog.getEventId().equals(eventId) &&
                        updatedLog.getStatus().equals("processed")
        ));
    }

    @Test
    void markFailedUpdatesStatusForRetryableError() {
        // Arrange
        String eventId = "evt_123";
        String errorMessage = "Some error occurred";
        boolean retryable = true;
        StripeAuditLog log = new StripeAuditLog();
        log.setEventId(eventId);
        log.setStatus("pending");
        when(auditLogRepository.findById(eventId)).thenReturn(Optional.of(log));

        // Act
        stripeAuditLogService.markFailed(eventId, errorMessage, retryable);

        // Assert
        verify(auditLogRepository).save(argThat(updatedLog ->
                updatedLog.getEventId().equals(eventId) &&
                        updatedLog.getStatus().equals("retry_pending") &&
                        updatedLog.getErrorMessage().equals(errorMessage) &&
                        updatedLog.getRetryCount() == 1
        ));
    }

    @Test
    void markFailedUpdatesStatusForNonRetryableError() {
        // Arrange
        String eventId = "evt_123";
        String errorMessage = "Some fatal error occurred";
        boolean retryable = false;
        StripeAuditLog log = new StripeAuditLog();
        log.setEventId(eventId);
        log.setStatus("pending");
        when(auditLogRepository.findById(eventId)).thenReturn(Optional.of(log));

        // Act
        stripeAuditLogService.markFailed(eventId, errorMessage, retryable);

        // Assert
        verify(auditLogRepository).save(argThat(updatedLog ->
                updatedLog.getEventId().equals(eventId) &&
                        updatedLog.getStatus().equals("error") &&
                        updatedLog.getErrorMessage().equals(errorMessage) &&
                        updatedLog.getRetryCount() == 1
        ));
    }

    @Test
    void getLogsForRetryReturnsRetryPendingLogs() {
        // Arrange
        int maxRetries = 3;
        List<StripeAuditLog> retryLogs = Arrays.asList(new StripeAuditLog(), new StripeAuditLog());
        when(auditLogRepository.findTop20ByStatusAndRetryCountLessThanOrderByCreatedAtAsc("retry_pending", maxRetries))
                .thenReturn(retryLogs);

        // Act
        List<StripeAuditLog> logs = stripeAuditLogService.getLogsForRetry(maxRetries);

        // Assert
        assertEquals(2, logs.size());
        verify(auditLogRepository).findTop20ByStatusAndRetryCountLessThanOrderByCreatedAtAsc("retry_pending", maxRetries);
    }

    @Test
    void logCustomSavesCustomLog() {
        // Arrange
        String eventId = "evt_custom_123";;
        String title = "Custom Event";
        String message = "Custom message content";

        StripeAuditLog expectedLog = new StripeAuditLog();
        expectedLog.setEventId(eventId);
        expectedLog.setEventType(title);
        expectedLog.setRawPayload(message);
        expectedLog.setStatus("info");
        expectedLog.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS)); // default expiration in the service

        when(auditLogRepository.save(any(StripeAuditLog.class))).thenReturn(expectedLog);

        // Act
        stripeAuditLogService.logCustom(title, message);

        // Assert
        verify(auditLogRepository).save(argThat(savedLog ->
                savedLog.getEventType().equals(title) &&
                        savedLog.getRawPayload().equals(message) &&
                        savedLog.getStatus().equals("info")
        ));
    }

    @Test
    void logEventSetsCorrectExpiryTime() {
        // Arrange
        Event event = mock(Event.class);
        when(event.getId()).thenReturn("evt_123");
        when(event.getType()).thenReturn("payment_intent.created");

        String rawPayload = "{\"data\": \"sample payload\"}";

        StripeAuditLog expectedLog = new StripeAuditLog();
        expectedLog.setEventId("evt_123");
        expectedLog.setEventType("payment_intent.created");
        expectedLog.setRawPayload(rawPayload);
        expectedLog.setStatus("pending");
        expectedLog.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS)); // Default 30 days TTL

        when(auditLogRepository.save(any(StripeAuditLog.class))).thenReturn(expectedLog);

        // Act
        stripeAuditLogService.logEvent(event, rawPayload);

        // Assert expiry time is set correctly
        verify(auditLogRepository).save(argThat(log ->
                log.getExpiresAt().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)) &&
                        log.getExpiresAt().isBefore(Instant.now().plus(31, ChronoUnit.DAYS))
        ));
    }
}
