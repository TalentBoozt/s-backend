package com.talentboozt.s_backend.Service.SYS_TRACKING.monitor;

import com.talentboozt.s_backend.DTO.SYS_TRACKING.monitor.*;
import com.talentboozt.s_backend.Model.SYS_TRACKING.TrackingEvent;
import com.talentboozt.s_backend.Repository.SYS_TRACKING.TrackingEventRepository;
import com.talentboozt.s_backend.Repository._private.UserActivityRepository;
import com.talentboozt.s_backend.Repository.common.LoginRepository;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitoringService {

    private final TrackingEventRepository eventRepo;
    private final UserActivityRepository activityRepo;
    private final LoginRepository loginRepo;

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
}
