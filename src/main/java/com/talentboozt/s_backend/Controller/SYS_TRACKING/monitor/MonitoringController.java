package com.talentboozt.s_backend.Controller.SYS_TRACKING.monitor;

import com.talentboozt.s_backend.DTO.SYS_TRACKING.monitor.*;
import com.talentboozt.s_backend.Service.SYS_TRACKING.monitor.MonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final MonitoringService monitoringService;

    @GetMapping("/overview")
    public DashboardOverviewDTO getOverview(
            @RequestParam String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.getOverview(trackingId, from, to);
    }

    @GetMapping("/page-views")
    public List<TimeSeriesPoint> getPageViews(
            @RequestParam String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.getPageViews(trackingId, from, to);
    }

    @GetMapping("/page-clicks")
    public List<TimeSeriesPoint> getClicks(
            @RequestParam String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.getPageClicks(trackingId, from, to);
    }

    @GetMapping("/page-performance")
    public List<TimeSeriesPoint> getPagePerformance(
            @RequestParam String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.getPagePerformance(trackingId, from, to);
    }

    @GetMapping("/event-types")
    public List<EventTypeCount> getEventTypes(
            @RequestParam String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.getEventCounts(trackingId, from, to);
    }

    @GetMapping("/performance")
    public PerformanceMetricsDTO getPerformance(
            @RequestParam String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.getPerformance(trackingId, from, to);
    }

    @GetMapping("/sessions")
    public List<SessionViewDTO> getSessionViews(
            @RequestParam String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.getSessionViews(trackingId, from, to);
    }

    @GetMapping("/geo")
    public List<LoginLocationAggregateDTO> getGeoData(
            @RequestParam String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.getGeoLocationCounts(trackingId, from, to);
    }

    @GetMapping("/devices/deprecated")
    public Map<String, Long> getDeviceInfoDeprecated(@RequestParam String trackingId) {
        return monitoringService.getDeviceInfo(trackingId);
    }

    @GetMapping("/browsers/deprecated")
    public Map<String, Long> getBrowserStatsDeprecated(@RequestParam String trackingId) {
        return monitoringService.getBrowserStats(trackingId);
    }

    @GetMapping("/browsers")
    public List<DeviceBrowserStatDTO> getBrowserStats(
            @RequestParam String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.aggregateBrowserStats(trackingId, from, to);
    }

    @GetMapping("/devices")
    public List<DeviceBrowserStatDTO> getDeviceStats(
            @RequestParam String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.aggregateDeviceStats(trackingId, from, to);
    }

    @GetMapping("/screen-sizes")
    public List<ScreenResolutionCount> getScreenSizeStats(
            @RequestParam String trackingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return monitoringService.aggregateScreenResolutions(trackingId, from, to);
    }

    @GetMapping("/session-details")
    public SessionViewDetail getSessionDetails(
            @RequestParam String trackingId,
            @RequestParam String sessionId) throws ChangeSetPersister.NotFoundException {
        return monitoringService.getSessionDetails(trackingId, sessionId);
    }
}
