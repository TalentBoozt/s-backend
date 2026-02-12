package com.talentboozt.s_backend.domains._private.service;

import com.talentboozt.s_backend.domains._private.model.UserActivity;
import com.talentboozt.s_backend.domains._private.repository.mongodb.UserActivityRepository;
import com.talentboozt.s_backend.domains.audit_logs.service.ClientActAuditLogService;
import com.talentboozt.s_backend.shared.utils.EncryptionUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserActivityService {

    @Autowired
    private UserActivityRepository repository;

    @Autowired
    private ClientActAuditLogService clientActAuditLogService;

    @Value("${audit.expire-after-days:30}")
    private long expireAfterDays;

    public void logUserActivity(String userId, String ipAddress, String endpointAccessed) {
        try {
            String encryptedIp = EncryptionUtility.encrypt(ipAddress);
            LocalDateTime recentWindow = LocalDateTime.now().minusMinutes(1);

            Optional<UserActivity> recentActivity = repository.findRecentActivity(encryptedIp, endpointAccessed, recentWindow);

            UserActivity activity;
            if (recentActivity.isPresent()) {
                activity = recentActivity.get();
            } else {
                activity = new UserActivity();
                activity.setUserId(userId);
                activity.setEncryptedIpAddress(encryptedIp);
                activity.setEndpointAccessed(endpointAccessed);
                activity.setTimestamp(LocalDateTime.now());
            }
            activity.setLastActive(LocalDateTime.now());
            activity.setExpiresAt(Instant.now().plus(expireAfterDays, ChronoUnit.DAYS));
            repository.save(activity);
        } catch (Exception e) {
            clientActAuditLogService.log(userId, ipAddress, null, "Error logging user activity", "UserActivityService", Map.of("error", e.getMessage()));
        }
    }

    public String decryptIpAddress(String encryptedIp) {
        try {
            return EncryptionUtility.decrypt(encryptedIp);
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    public List<Map<String, String>> getUserActivities(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<UserActivity> activities = repository.findAll(pageable);

        return activities.stream().map(activity -> {
            Map<String, String> data = new HashMap<>();
            data.put("userId", activity.getUserId() != null ? activity.getUserId() : "Unknown");
            data.put("ipAddress", activity.getEncryptedIpAddress() != null
                    ? decryptIpAddress(activity.getEncryptedIpAddress())
                    : "Unknown");
            data.put("timestamp", activity.getTimestamp() != null
                    ? activity.getTimestamp().toString()
                    : "N/A");
            data.put("lastActive", activity.getLastActive() != null
                    ? activity.getLastActive().toString()
                    : String.valueOf(LocalDateTime.now()));
            data.put("endpointAccessed", activity.getEndpointAccessed() != null
                    ? activity.getEndpointAccessed()
                    : "N/A");
            return data;
        }).collect(Collectors.toList());
    }

    public ResponseEntity<String> clearUserActivities() {
        repository.deleteAll();
        return ResponseEntity.ok("All user activities deleted");
    }

    public long getActiveUserCount() {
        LocalDateTime activeSince = LocalDateTime.now().minus(15, ChronoUnit.MINUTES);
        return repository.countActiveUsers(activeSince);
    }

    public Map<String, Long> getActivityOverTime(String interval, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<UserActivity> activities = repository.findAll(pageable);

        DateTimeFormatter formatter = interval.equals("hourly") ?
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH") :
                DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return activities.stream()
                .collect(Collectors.groupingBy(
                        activity -> activity.getTimestamp().format(formatter),
                        Collectors.counting()
                ));
    }

    public Integer getTotalElements() {
        return (int) repository.count();
    }
}

