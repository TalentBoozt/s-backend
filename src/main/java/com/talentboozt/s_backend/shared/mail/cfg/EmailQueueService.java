package com.talentboozt.s_backend.shared.mail.cfg;

import com.talentboozt.s_backend.domains.audit_logs.service.SchedulerLoggerService;
import com.talentboozt.s_backend.shared.mail.dto.EmailJob;
import com.talentboozt.s_backend.shared.mail.service.HTMLEmailService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Component
public class EmailQueueService {

    private final BlockingQueue<EmailJob> emailQueue = new LinkedBlockingQueue<>(1000); // Set a max queue size
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private HTMLEmailService emailService;

    @Autowired
    private SchedulerLoggerService logger;

    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 5000;
    private static final long BETWEEN_SEND_DELAY_MS = 1000;

    @PostConstruct
    public void startQueue() {
        scheduler.scheduleWithFixedDelay(this::processNextEmail, 0, BETWEEN_SEND_DELAY_MS, TimeUnit.MILLISECONDS);
    }

    public void queueEmail(EmailJob job) {
        boolean added = emailQueue.offer(job);
        if (!added) {
            logger.log("email-queue", "ERROR", "Queue full — dropping email to " + job.getTo());
        }
    }

    private void processNextEmail() {
        EmailJob job = emailQueue.poll();
        if (job == null) return;

        try {
            boolean sent = sendEmail(job);
            if (!sent) throw new RuntimeException("Failed to send email");

        } catch (Exception e) {
            job.setRetryCount(job.getRetryCount() + 1);

            if (job.getRetryCount() > MAX_RETRY_COUNT) {
                logger.log("email-queue", "ERROR", "Max retry exceeded for: " + job.getTo());
                return;
            }

            logger.log("email-queue", "ERROR", "Retrying email to " + job.getTo() + " (" + job.getRetryCount() + "/" + MAX_RETRY_COUNT + ")");

            scheduler.schedule(() -> {
                boolean requeued = emailQueue.offer(job);
                if (!requeued) {
                    logger.log("email-queue", "ERROR", "Retry queue full — dropping email to " + job.getTo());
                }
            }, RETRY_DELAY_MS, TimeUnit.MILLISECONDS);
        }
    }

    public int getQueueSize() {
        return emailQueue.size();
    }

    private boolean sendEmail(EmailJob job) {
        try {
            emailService.sendHtmlEmail(job.getTo(), job.getSubject(), job.getHtmlBody());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
