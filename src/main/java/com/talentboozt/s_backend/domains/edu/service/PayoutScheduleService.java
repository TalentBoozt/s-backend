package com.talentboozt.s_backend.domains.edu.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.talentboozt.s_backend.domains.edu.dto.finance.PayoutRequest;
import com.talentboozt.s_backend.domains.edu.dto.finance.RevenueSummaryDTO;
import com.talentboozt.s_backend.domains.edu.enums.EPayoutMethod;
import com.talentboozt.s_backend.domains.edu.model.EPayoutSchedule;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EPayoutScheduleRepository;

@Service
public class PayoutScheduleService {
    private static final Logger log = LoggerFactory.getLogger(PayoutScheduleService.class);

    private final EPayoutScheduleRepository scheduleRepository;
    private final EduFinanceService financeService;

    public PayoutScheduleService(EPayoutScheduleRepository scheduleRepository, EduFinanceService financeService) {
        this.scheduleRepository = scheduleRepository;
        this.financeService = financeService;
    }

    /**
     * Runs daily at 1 AM to process due payouts based on schedule.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processScheduledPayouts() {
        log.info("Starting scheduled payout processing...");
        Instant now = Instant.now();
        List<EPayoutSchedule> dueSchedules = scheduleRepository.findByActiveTrueAndNextScheduledAtBefore(now);
        
        for (EPayoutSchedule schedule : dueSchedules) {
            try {
                processSchedule(schedule);
                
                // Calculate next run
                Instant nextRun = calculateNextRun(schedule.getFrequency(), schedule.getDayTarget(), now);
                schedule.setLastProcessedAt(now);
                schedule.setNextScheduledAt(nextRun);
                scheduleRepository.save(schedule);
                
            } catch (Exception e) {
                log.error("Failed to process payout schedule for creator: {}", schedule.getCreatorId(), e);
            }
        }
        log.info("Finished processing {} scheduled payouts.", dueSchedules.size());
    }

    private void processSchedule(EPayoutSchedule schedule) {
        RevenueSummaryDTO summary = financeService.getRevenueSummary(schedule.getCreatorId());
        
        // Auto-payout only if there is available balance >= 25
        if (summary.getAvailableBalance() >= 25.0) {
            PayoutRequest request = new PayoutRequest();
            request.setAmount(summary.getAvailableBalance());
            request.setCurrency("USD");
            request.setMethod(EPayoutMethod.STRIPE); // Preference can be read from settings
            
            // This requests the payout. In an automated system, it may also auto-approve if configured so.
            financeService.requestPayout(schedule.getCreatorId(), request);
            log.info("Auto-requested payout of {} for creator {}", summary.getAvailableBalance(), schedule.getCreatorId());
        }
    }

    private Instant calculateNextRun(String frequency, String target, Instant from) {
        ZonedDateTime zdt = from.atZone(ZoneId.of("UTC"));
        if ("WEEKLY".equalsIgnoreCase(frequency)) {
            // target could be "MONDAY"
            java.time.DayOfWeek day = java.time.DayOfWeek.valueOf(target.toUpperCase());
            return zdt.with(TemporalAdjusters.next(day)).toInstant();
        } else if ("MONTHLY".equalsIgnoreCase(frequency)) {
            // target could be "1"
            int dayOfMonth = Integer.parseInt(target);
            ZonedDateTime nextMonth = zdt.plusMonths(1).withDayOfMonth(Math.min(dayOfMonth, 28)); // Safe fallback for 28
            return nextMonth.toInstant();
        }
        // Fallback to manual/inactive
        return zdt.plusDays(365).toInstant();
    }
}
