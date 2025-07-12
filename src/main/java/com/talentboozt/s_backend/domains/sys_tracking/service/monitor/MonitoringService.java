package com.talentboozt.s_backend.domains.sys_tracking.service.monitor;

import com.talentboozt.s_backend.domains.sys_tracking.dto.monitor.*;
import com.talentboozt.s_backend.domains.sys_tracking.model.TrackingEvent;
import com.talentboozt.s_backend.domains.sys_tracking.repository.TrackingEventRepository;
import com.talentboozt.s_backend.domains._private.repository.UserActivityRepository;
import com.talentboozt.s_backend.domains.common.repository.LoginRepository;
import com.talentboozt.s_backend.domains.auth.repository.CredentialsRepository;
import com.talentboozt.s_backend.domains.auth.repository.PermissionRepository;
import com.talentboozt.s_backend.domains.auth.repository.RoleRepository;
import com.talentboozt.s_backend.shared.utils.EncryptionUtility;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final TrackingEventRepository eventRepo;
    private final UserActivityRepository activityRepo;
    private final LoginRepository loginRepo;
    private final CredentialsRepository credentialsRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final MongoTemplate mongoTemplate;

    public DashboardOverviewDTO getOverview(String trackingId, Instant from, Instant to) {
        long activeNow = activityRepo.countByLastActiveAfter(Instant.now().minusSeconds(300));
        long dailyUsers = loginRepo.countDistinctUserIdByLoginDatesContaining(LocalDate.now().toString());
        long sessions = eventRepo.countDistinctSessionIdByTrackingIdAndTimestampBetween(trackingId, from, to);
        long errors = eventRepo.countByTrackingIdAndErrorMessageNotNullAndTimestampBetween(trackingId, from, to);

        return new DashboardOverviewDTO(activeNow, dailyUsers, sessions, errors);
    }

    public List<TimeSeriesPoint> getPageViews(String trackingId, Instant from, Instant to) {
        return eventRepo.aggregatePageViewsByTime(trackingId, from, to);
    }

    public List<TimeSeriesPoint> getPageClicks(String trackingId, Instant from, Instant to) {
        return eventRepo.aggregatePageClicksByTime(trackingId, from, to);
    }

    public List<TimeSeriesPoint> getPagePerformance(String trackingId, Instant from, Instant to) {
        return eventRepo.aggregatePagePerformanceByTime(trackingId, from, to);
    }

    public List<EventTypeCount> getEventCounts(String trackingId, Instant from, Instant to) {
        return eventRepo.countEventsGroupedByType(trackingId, from, to);
    }

    public PerformanceMetricsDTO getPerformance(String trackingId, Instant from, Instant to) {
        return eventRepo.aggregatePerformanceMetrics(trackingId, from, to);
    }

    public List<SessionViewDTO> getSessionViews(String trackingId, Instant from, Instant to) {
        return eventRepo.aggregateSessionViews(trackingId, from, to);
    }

    public List<LoginLocationAggregateDTO> getGeoLocationCounts(String trackingId, Instant from, Instant to) {
        return loginRepo.aggregateLoginLocations(trackingId, from, to);
    }

    public Map<String, Long> getDeviceInfo(String trackingId) {
        return loginRepo.countByPlatform(trackingId);
    }

    public Map<String, Long> getBrowserStats(String trackingId) {
        return eventRepo.countByBrowser(trackingId);
    }

    public List<DeviceBrowserStatDTO> aggregateBrowserStats(String trackingId, Instant from, Instant to) {
        List<TrackingEvent> events = eventRepo
                .findByTrackingIdAndTimestampBetween(trackingId, from, to);

        Map<String, Long> browserCounts = new HashMap<>();

        for (TrackingEvent event : events) {
            if (event.getBrowser() != null) {
                UserAgent userAgent = UserAgent.parseUserAgentString(event.getBrowser());
                String browserName = userAgent.getBrowser().getName();

                browserCounts.merge(browserName, 1L, Long::sum);
            }
        }

        return browserCounts.entrySet().stream()
                .map(e -> new DeviceBrowserStatDTO(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(DeviceBrowserStatDTO::getCount).reversed())
                .collect(Collectors.toList());
    }

    public List<DeviceBrowserStatDTO> aggregateDeviceStats(String trackingId, Instant from, Instant to) {
        List<TrackingEvent> events = eventRepo
                .findByTrackingIdAndTimestampBetween(trackingId, from, to);

        Map<String, Long> deviceCounts = new HashMap<>();

        for (TrackingEvent event : events) {
            if (event.getBrowser() != null) {
                UserAgent userAgent = UserAgent.parseUserAgentString(event.getBrowser());
                OperatingSystem os = userAgent.getOperatingSystem();

                String deviceType = switch (os.getDeviceType()) {
                    case COMPUTER -> "Desktop";
                    case TABLET -> "Tablet";
                    case MOBILE -> "Mobile";
                    default -> "Unknown";
                };

                deviceCounts.merge(deviceType, 1L, Long::sum);
            }
        }

        return deviceCounts.entrySet().stream()
                .map(e -> new DeviceBrowserStatDTO(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(DeviceBrowserStatDTO::getCount).reversed())
                .collect(Collectors.toList());
    }

    public List<ScreenResolutionCount> aggregateScreenResolutions(String trackingId, Instant from, Instant to) { return eventRepo.countByScreenResolution(trackingId, from, to); }

    public SessionViewDetail getSessionDetails(String trackingId, String sessionId) throws ChangeSetPersister.NotFoundException {
        List<TrackingEvent> events = eventRepo.findSessionEvents(trackingId, sessionId);

        if (events.isEmpty()) {
            throw new ChangeSetPersister.NotFoundException();
        }

        TrackingEvent first = events.get(0);
        TrackingEvent last = events.get(events.size() - 1);

        // Deduplicate URLs in order
        List<String> orderedUrls = events.stream()
                .map(TrackingEvent::getUrl)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<SessionEvent> sessionEvents = events.stream()
                .map(e -> new SessionEvent(
                        e.getTimestamp().toString(),
                        e.getEventType(),
                        e.getUrl(),
                        e.getElementText()
                ))
                .toList();

        List<SessionError> errors = events.stream()
                .filter(e -> e.getErrorMessage() != null)
                .map(e -> new SessionError(
                        e.getErrorMessage(),
                        e.getErrorSource(),
                        e.getErrorLine(),
                        e.getErrorColumn(),
                        e.getRejectionReason()
                ))
                .toList();

        return new SessionViewDetail(
                sessionId,
                first.getUserId(),
                Duration.between(first.getTimestamp(), last.getTimestamp()).getSeconds(),
                events.size(),
                first.getBrowser(),
                first.getScreenResolution(),
                first.getLanguage(),
                first.getReferrer(),
                averageNonZero(events.stream().map(TrackingEvent::getDomLoadTime).toList()),
                averageNonZero(events.stream().map(TrackingEvent::getFullLoadTime).toList()),
                averageNonZero(events.stream().map(TrackingEvent::getTtfb).toList()),
                errors,
                sessionEvents
        );
    }
    private long averageNonZero(List<Long> values) {
        return (long) values.stream()
                .filter(v -> v != null && v > 0)
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);
    }

    public Map<String, Long> getBasicStats() {
        long totalUsers = credentialsRepository.count();
        long totalRoles = roleRepository.count();
        long totalPermissions = permissionRepository.count();
        long disabledUsers = credentialsRepository.countByDisabledTrue();

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalRoles", totalRoles);
        stats.put("totalPermissions", totalPermissions);
        stats.put("disabledUsers", disabledUsers);
        return stats;
    }

    public List<RoleUserCountDTO> getActiveUsersByRole() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("active").is(true)),
                Aggregation.unwind("roles"),
                Aggregation.group("roles").count().as("count"),
                Aggregation.project("count").and("_id").as("role")
        );
        AggregationResults<RoleUserCountDTO> results =
                mongoTemplate.aggregate(agg, "portal_credentials", RoleUserCountDTO.class);
        return results.getMappedResults();
    }

    public List<Document> getPermissionUsage() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.unwind("permissions"),
                Aggregation.group("permissions.name").count().as("count"),
                Aggregation.project("count").and("_id").as("permission")
        );
        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "portal_credentials", Document.class);
        return results.getMappedResults();
    }

    public List<SuspiciousActivityDTO> getSuspiciousActivities() {
        return List.of(
                new SuspiciousActivityDTO("User123", "Admin", "CAN_MANAGE_USERS", "Accessed admin page"),
                new SuspiciousActivityDTO("User123", "Job Seeker", "CAN_POST_JOBS", "Posted multiple jobs"),
                new SuspiciousActivityDTO("Ab9ca123", "Job Seeker", "CAN_CREATE_COURSES", "Modified course"),
                new SuspiciousActivityDTO("User123", "Admin", null, null)
        );
    }

    /** Suspicious 1: Abnormal session durations */
    public List<Document> detectAbnormalSessionDurations(long minSeconds, long maxSeconds) {
        ProjectionOperation project = Aggregation.project()
                .andExpression("$sessionEnd").as("sessionEnd")
                .andExpression("$sessionStart").as("sessionStart")
                .andExpression("$userId").as("userId")
                .andExpression("$endpointAccessed").as("endpointAccessed")
                .andExpression("$sessionEnd - $sessionStart").as("durationMs");

        if (maxSeconds == 0) {
            MatchOperation match = Aggregation.match(
                    new Criteria().orOperator(
                            Criteria.where("durationMs").lt(minSeconds * 1000)
                    )
            );

            Aggregation agg = Aggregation.newAggregation(project, match);
            return mongoTemplate.aggregate(agg, "portal_user_activity", Document.class).getMappedResults();
        }
        MatchOperation match = Aggregation.match(
                new Criteria().orOperator(
                        Criteria.where("durationMs").lt(minSeconds * 1000),
                        Criteria.where("durationMs").gt(maxSeconds * 1000)
                )
        );

        Aggregation agg = Aggregation.newAggregation(project, match);
        return mongoTemplate.aggregate(agg, "portal_user_activity", Document.class).getMappedResults();
    }

    /** Suspicious 2: High frequency endpoint access */
    public List<Document> detectHighFrequencyAccess(int thresholdPerMinute) {
        GroupOperation groupByUserAndMinute = Aggregation.group("userId")
                .first("userId").as("userId")
                .count().as("totalAccesses");

        MatchOperation match = Aggregation.match(Criteria.where("totalAccesses").gt(thresholdPerMinute));

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timestamp").gt(LocalDateTime.now().minusMinutes(1))),
                groupByUserAndMinute,
                match
        );

        return mongoTemplate.aggregate(agg, "portal_user_activity", Document.class).getMappedResults();
    }

    /** Suspicious 3: Multiple IPs per user in short time */
    public List<Document> detectMultipleIpsPerUser(long timeWindowMinutes) {
        Instant recentWindow = Instant.now().minusSeconds(timeWindowMinutes * 60);

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timestamp").gt(LocalDateTime.ofInstant(recentWindow, ZoneOffset.UTC))),
                Aggregation.group("userId")
                        .addToSet("encryptedIpAddress").as("uniqueIps")
                        .count().as("requestCount"),
                Aggregation.project("uniqueIps", "requestCount")
                        .and("_id").as("userId")
                        .andExpression("size(uniqueIps)").as("ipCount"),
                Aggregation.match(Criteria.where("ipCount").gt(1))
        );

        List<Document> results = mongoTemplate.aggregate(agg, "portal_user_activity", Document.class).getMappedResults();

        // Decrypt IPs and replace the list
        for (Document doc : results) {
            List<String> encryptedIps = (List<String>) doc.get("uniqueIps");

            List<String> decryptedIps = encryptedIps.stream()
                    .map(this::decryptIpAddress)
                    .collect(Collectors.toList());

            // Optionally convert to newline-separated string
            doc.put("uniqueIps", String.join("\n", decryptedIps));

            // Optionally replace encrypted list with decrypted one
//            doc.put("uniqueIpsString", decryptedIps);
        }

        return results;
    }

    /** Helper method to decrypt IP addresses */
    public String decryptIpAddress(String encryptedIp) {
        try {
            return EncryptionUtility.decrypt(encryptedIp);
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    /** Suspicious 4: Geolocation anomaly per user */
    public List<Document> detectGeolocationAnomalies(long timeWindowMinutes) {
        Instant recentWindow = Instant.now().minusSeconds(timeWindowMinutes * 60);

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timestamp").gt(recentWindow)),
                Aggregation.group("userId")
                        .addToSet("country").as("uniqueCountries")
                        .addToSet("city").as("uniqueCities"),
                Aggregation.project("uniqueCountries", "uniqueCities")
                        .and("_id").as("userId")
                        .andExpression("size(uniqueCountries)").as("countryCount")
                        .andExpression("size(uniqueCities)").as("cityCount"),
                Aggregation.match(new Criteria().orOperator(
                        Criteria.where("countryCount").gt(1),
                        Criteria.where("cityCount").gt(1)
                ))
        );

        return mongoTemplate.aggregate(agg, "events", Document.class).getMappedResults();
    }

    /** Suspicious 5: Excessive JavaScript errors per user */
    public List<Document> detectClientErrorsPerUser(int threshold) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("errorMessage").ne(null)),
                Aggregation.group("userId")
                        .count().as("errorCount"),
                Aggregation.match(Criteria.where("errorCount").gt(threshold))
        );

        return mongoTemplate.aggregate(agg, "events", Document.class).getMappedResults();
    }

    /** Suspicious 6: Anonymous users hitting protected endpoints */
    public List<Document> detectAnonymousAccessingProtectedEndpoints(List<String> protectedEndpoints) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("userId").is("Anonymous"),
                        Criteria.where("endpointAccessed").in(protectedEndpoints)
                )),
                Aggregation.project("userId", "endpointAccessed", "timestamp")
        );

        return mongoTemplate.aggregate(agg, "portal_user_activity", Document.class).getMappedResults();
    }
}
