package com.talentboozt.s_backend.domains.sys_tracking.service.monitor;

import com.talentboozt.s_backend.domains._private.dto.PagedResponse;
import com.talentboozt.s_backend.domains._private.repository.mongodb.UserActivityRepository;
import com.talentboozt.s_backend.domains.auth.model.*;
import com.talentboozt.s_backend.domains.auth.service.RoleService;
import com.talentboozt.s_backend.domains.common.repository.mongodb.LoginRepository;
import com.talentboozt.s_backend.domains.sys_tracking.dto.monitor.*;
import com.talentboozt.s_backend.domains.sys_tracking.model.TrackingEvent;
import com.talentboozt.s_backend.domains.sys_tracking.repository.mongodb.TrackingEventRepository;
import com.talentboozt.s_backend.domains.auth.repository.mongodb.CredentialsRepository;
import com.talentboozt.s_backend.domains.auth.repository.mongodb.PermissionRepository;
import com.talentboozt.s_backend.domains.auth.repository.mongodb.RoleRepository;
import com.talentboozt.s_backend.shared.utils.EncryptionUtility;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.bson.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    private final RoleService roleService;
    private final MongoTemplate mongoTemplate;

    public List<org.bson.Document> getRawEvents(int limit) {
        org.springframework.data.mongodb.core.query.Query query = new org.springframework.data.mongodb.core.query.Query()
                .limit(limit);
        return mongoTemplate.find(query, org.bson.Document.class, "events");
    }

    public String getDebugStats() {
        try {
            long total = mongoTemplate.getCollection("events").countDocuments();

            // Count for 2026
            java.time.Instant startOf2026 = java.time.Instant.parse("2026-01-01T00:00:00Z");
            org.springframework.data.mongodb.core.query.Query query2026 = new org.springframework.data.mongodb.core.query.Query(
                    org.springframework.data.mongodb.core.query.Criteria.where("timestamp").gte(startOf2026));
            long recentCount = mongoTemplate.count(query2026, TrackingEvent.class);

            // Simplified aggregation to get IDs
            java.util.List<org.bson.Document> recentIdStats = mongoTemplate.getCollection("events").aggregate(
                    java.util.Arrays.asList(
                            new org.bson.Document("$match",
                                    new org.bson.Document("timestamp",
                                            new org.bson.Document("$gte", java.util.Date.from(startOf2026)))),
                            new org.bson.Document("$group",
                                    new org.bson.Document("_id", "$trackingId").append("count",
                                            new org.bson.Document("$sum", 1)))))
                    .into(new java.util.ArrayList<>());

            return "Total documents: " + total + " | Recent (2026+): " + recentCount + " | Recent IDs: "
                    + recentIdStats.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public DashboardOverviewDTO getOverview(String trackingId, Instant from, Instant to) {
        long activeNow = activityRepo.countByLastActiveAfter(Instant.now().minusSeconds(300));
        long dailyUsers = countUniqueUsersByDate(LocalDate.now().toString());
        Long sessions = eventRepo.countDistinctSessions(trackingId, from, to);
        Long errors = eventRepo.countByTrackingIdAndEventTypeAndTimestampBetweenFixed(trackingId, "error", from, to);

        return new DashboardOverviewDTO(activeNow, dailyUsers, sessions != null ? sessions : 0L,
                errors != null ? errors : 0L);
    }

    public long countUniqueUsersByDate(String date) {
        UniqueUserCountDTO result = loginRepo.countDistinctUserIdByEventDate(date);
        return result != null ? result.getUniqueUserCount() : 0;
    }

    public List<TimeSeriesPoint> getPageViews(String trackingId, Instant from, Instant to, String granularity) {
        return eventRepo.aggregatePageViewsByTime(trackingId, from, to, granularity);
    }

    public List<TimeSeriesPoint> getPageClicks(String trackingId, Instant from, Instant to, String granularity) {
        return eventRepo.aggregatePageClicksByTime(trackingId, from, to, granularity);
    }

    public List<TimeSeriesPoint> getPagePerformance(String trackingId, Instant from, Instant to, String granularity) {
        return eventRepo.aggregatePagePerformanceByTime(trackingId, from, to, granularity);
    }

    public List<EventTypeCount> getEventCounts(String trackingId, Instant from, Instant to) {
        return eventRepo.countEventsGroupedByType(trackingId, from, to);
    }

    public PerformanceMetricsDTO getPerformance(String trackingId, Instant from, Instant to) {
        return eventRepo.aggregatePerformanceMetrics(trackingId, from, to);
    }

    public PagedResponse<SessionViewDTO> getSessionViews(String trackingId, Instant from, Instant to, int page,
            int size) {
        Long totalRes = eventRepo.countDistinctSessions(trackingId, from, to);
        long total = totalRes != null ? totalRes : 0L;

        List<SessionViewDTO> items = eventRepo.aggregateSessionViewsPaginated(trackingId, from, to, page * size, size);

        return new PagedResponse<>(items, total);
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

    public List<ScreenResolutionCount> aggregateScreenResolutions(String trackingId, Instant from, Instant to) {
        return eventRepo.countByScreenResolution(trackingId, from, to);
    }

    public SessionViewDetail getSessionDetails(String trackingId, String sessionId)
            throws ChangeSetPersister.NotFoundException {
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
                        e.getElementText()))
                .toList();

        List<SessionError> errors = events.stream()
                .filter(e -> e.getErrorMessage() != null)
                .map(e -> new SessionError(
                        e.getErrorMessage(),
                        e.getErrorSource(),
                        e.getErrorLine(),
                        e.getErrorColumn(),
                        e.getRejectionReason()))
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
                sessionEvents);
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
                Aggregation.project("count").and("_id").as("role"));
        AggregationResults<RoleUserCountDTO> results = mongoTemplate.aggregate(agg, "portal_credentials",
                RoleUserCountDTO.class);
        return results.getMappedResults();
    }

    public List<Document> getPermissionUsage() {
        List<CredentialsModel> users = credentialsRepository.findAll();
        List<RoleModel> allRoles = roleService.getAllRoles();

        // Safely build role → permissions map
        Map<String, List<String>> rolePermissionsMap = allRoles.stream()
                .filter(role -> role.getName() != null)
                .collect(Collectors.toMap(
                        RoleModel::getName,
                        role -> Optional.ofNullable(role.getPermissions()).orElse(Collections.emptyList())));

        // Count permission usage across all users
        Map<String, Integer> permissionUsageMap = new HashMap<>();

        for (CredentialsModel user : users) {
            if (user.getRoles() == null)
                continue;

            Set<String> userPermissions = new HashSet<>();
            for (String role : user.getRoles()) {
                List<String> permissions = rolePermissionsMap.getOrDefault(role, Collections.emptyList());
                userPermissions.addAll(permissions);
            }

            for (String permission : userPermissions) {
                permissionUsageMap.merge(permission, 1, Integer::sum);
            }
        }

        // Convert to list of Document for Mongo-style results
        List<Document> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : permissionUsageMap.entrySet()) {
            Document doc = new Document();
            doc.put("permission", entry.getKey());
            doc.put("count", entry.getValue());
            result.add(doc);
        }

        return result;
    }

    public List<SuspiciousActivityDTO> getSuspiciousActivities() {
        return List.of(
                new SuspiciousActivityDTO("User123", "Admin", "CAN_MANAGE_USERS", "Accessed admin page"),
                new SuspiciousActivityDTO("User123", "Job Seeker", "CAN_POST_JOBS", "Posted multiple jobs"),
                new SuspiciousActivityDTO("Ab9ca123", "Job Seeker", "CAN_CREATE_COURSES", "Modified course"),
                new SuspiciousActivityDTO("User123", "Admin", null, null));
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
                            Criteria.where("durationMs").lt(minSeconds * 1000)));

            Aggregation agg = Aggregation.newAggregation(project, match);
            return mongoTemplate.aggregate(agg, "portal_user_activity", Document.class).getMappedResults();
        }
        MatchOperation match = Aggregation.match(
                new Criteria().orOperator(
                        Criteria.where("durationMs").lt(minSeconds * 1000),
                        Criteria.where("durationMs").gt(maxSeconds * 1000)));

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
                Aggregation.match(
                        Criteria.where("timestamp").gt(Objects.requireNonNull(LocalDateTime.now().minusMinutes(1)))),
                groupByUserAndMinute,
                match);

        return mongoTemplate.aggregate(agg, "portal_user_activity", Document.class).getMappedResults();
    }

    /** Suspicious 3: Multiple IPs per user in short time */
    public List<Document> detectMultipleIpsPerUser(long timeWindowMinutes) {
        Instant recentWindow = Instant.now().minusSeconds(timeWindowMinutes * 60);

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("timestamp")
                        .gt(Objects.requireNonNull(LocalDateTime.ofInstant(recentWindow, ZoneOffset.UTC)))),
                Aggregation.group("userId")
                        .addToSet("encryptedIpAddress").as("uniqueIps")
                        .count().as("requestCount"),
                Aggregation.project("uniqueIps", "requestCount")
                        .and("_id").as("userId")
                        .andExpression("size(uniqueIps)").as("ipCount"),
                Aggregation.match(Criteria.where("ipCount").gt(1)));

        List<Document> results = mongoTemplate.aggregate(agg, "portal_user_activity", Document.class)
                .getMappedResults();

        // Decrypt IPs and replace the list
        for (Document doc : results) {
            List<String> encryptedIps = (List<String>) doc.get("uniqueIps");

            List<String> decryptedIps = encryptedIps.stream()
                    .map(this::decryptIpAddress)
                    .collect(Collectors.toList());

            // Optionally convert to newline-separated string
            doc.put("uniqueIps", String.join("\n", decryptedIps));

            // Optionally replace encrypted list with decrypted one
            // doc.put("uniqueIpsString", decryptedIps);
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
                Aggregation.match(Criteria.where("timestamp").gt(Objects.requireNonNull(recentWindow))),
                Aggregation.group("userId")
                        .addToSet("country").as("uniqueCountries")
                        .addToSet("city").as("uniqueCities"),
                Aggregation.project("uniqueCountries", "uniqueCities")
                        .and("_id").as("userId")
                        .andExpression("size(uniqueCountries)").as("countryCount")
                        .andExpression("size(uniqueCities)").as("cityCount"),
                Aggregation.match(new Criteria().orOperator(
                        Criteria.where("countryCount").gt(1),
                        Criteria.where("cityCount").gt(1))));

        return mongoTemplate.aggregate(agg, "events", Document.class).getMappedResults();
    }

    /** Suspicious 5: Excessive JavaScript errors per user */
    public List<Document> detectClientErrorsPerUser(int threshold) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("errorMessage").ne(null)),
                Aggregation.group("userId")
                        .count().as("errorCount"),
                Aggregation.match(Criteria.where("errorCount").gt(threshold)));

        return mongoTemplate.aggregate(agg, "events", Document.class).getMappedResults();
    }

    /** Suspicious 6: Anonymous users hitting protected endpoints */
    public List<Document> detectAnonymousAccessingProtectedEndpoints(List<String> protectedEndpoints) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("userId").is("Anonymous"),
                        Criteria.where("endpointAccessed").in(Objects.requireNonNull(protectedEndpoints)))),
                Aggregation.project("userId", "endpointAccessed", "timestamp"));

        return mongoTemplate.aggregate(agg, "portal_user_activity", Document.class).getMappedResults();
    }

    public void backfillMissingRoles() {
        Query query = new Query();
        query.addCriteria(new Criteria().orOperator(
                Criteria.where("roles").exists(false),
                Criteria.where("roles").is(null),
                Criteria.where("roles").size(0)));

        List<CredentialsModel> usersMissingRoles = mongoTemplate.find(query, CredentialsModel.class,
                "portal_credentials");

        for (CredentialsModel user : usersMissingRoles) {
            // Determine if this user is a social-auth user
            boolean isSocialAuth = user.getAccessedPlatforms() != null &&
                    user.getAccessedPlatforms().size() == 1 &&
                    "ALL".equals(user.getAccessedPlatforms().get(0));

            boolean isLearner = user.getAccessedPlatforms() != null &&
                    user.getAccessedPlatforms().contains("TrainingPlatform");

            boolean isTrainer = user.getAccessedPlatforms() != null &&
                    user.getAccessedPlatforms().contains("TrainingPlatform");

            // Set default role(s) based on logic — here assuming social auth users are
            // "SOCIAL_LOGGER"
            List<String> defaultRoles = new ArrayList<>();
            if (isSocialAuth && isLearner) {
                defaultRoles.addAll(Arrays.asList("CANDIDATE", "LEARNER", "SOCIAL_LOGGER"));
            } else if (isSocialAuth && isTrainer) {
                defaultRoles.addAll(Arrays.asList("TRAINER", "SOCIAL_LOGGER"));
            } else if (isLearner) {
                defaultRoles.add("LEARNER");
            } else if (isTrainer) {
                defaultRoles.add("TRAINER");
            } else {
                defaultRoles.add("NOT_SPECIFIED");
            }

            Update update = new Update().set("roles", defaultRoles);

            // Optionally remove deprecated "role" field
            // update.unset("role");

            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(user.getId())),
                    update,
                    "portal_credentials");
        }
    }

}
