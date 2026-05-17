package com.talentboozt.s_backend.domains.notifications.service;

import com.talentboozt.s_backend.domains.notifications.model.NotificationModel;
import com.talentboozt.s_backend.domains.notifications.repository.mongodb.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationModel createNotification(String userId, String title, String message, String type, String actionUrl) {
        NotificationModel notification = new NotificationModel();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setActionUrl(actionUrl);
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        return notificationRepository.save(notification);
    }

    public void notifyRecruiterNewApplicant(String recruiterId, String candidateName, String jobTitle, String applicationId) {
        createNotification(
            recruiterId,
            "New Applicant: " + candidateName,
            candidateName + " has applied for the " + jobTitle + " role.",
            "JOB_MATCH",
            "/recruiter/pipeline?applicationId=" + applicationId
        );
    }

    public void notifyRecruiterInterviewScheduled(String recruiterId, String candidateName, Instant time) {
        createNotification(
            recruiterId,
            "Interview Scheduled",
            "You have an interview with " + candidateName + " at " + time.toString(),
            "APPLICATION_STATUS",
            "/recruiter/pipeline"
        );
    }

    public List<NotificationModel> getByUser(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(String id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
