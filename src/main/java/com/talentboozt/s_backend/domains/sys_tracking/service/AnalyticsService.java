package com.talentboozt.s_backend.domains.sys_tracking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.talentboozt.s_backend.domains.sys_tracking.dto.monitor.*;
import com.talentboozt.s_backend.domains.sys_tracking.repository.mongodb.TrackingEventRepository;

import java.time.Instant;
import java.util.List;

/**
 * Analytics Service - Business logic for analytics queries
 * Provides high-level analytics for all v2.0 features
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TrackingEventRepository repository;

    // ========== A/B TESTING ANALYTICS ==========

    public ExperimentResultsDTO getExperimentResults(String trackingId, String experimentId) {
        ExperimentResultsDTO results = new ExperimentResultsDTO();
        results.setExperimentId(experimentId);

        // Get assignments
        List<ExperimentVariantCount> assignments = repository.countExperimentAssignments(trackingId, experimentId);
        results.setAssignments(assignments);

        // Get conversions
        List<ExperimentConversionStats> conversions = repository.aggregateExperimentConversions(trackingId,
                experimentId);
        results.setConversions(conversions);

        // Calculate overall conversion rate
        long totalAssignments = assignments.stream().mapToLong(ExperimentVariantCount::getCount).sum();
        long totalConversions = conversions.stream().mapToLong(ExperimentConversionStats::getConversions).sum();

        if (totalAssignments > 0) {
            results.setConversionRate((double) totalConversions / totalAssignments * 100);
        }

        return results;
    }

    // ========== FUNNEL ANALYTICS ==========

    public FunnelAnalysisDTO getFunnelAnalysis(String trackingId, String funnelId) {
        FunnelAnalysisDTO analysis = new FunnelAnalysisDTO();
        analysis.setFunnelId(funnelId);

        // Get step stats
        List<FunnelStepStats> steps = repository.aggregateFunnelSteps(trackingId, funnelId);

        // Calculate dropoff rates
        for (int i = 0; i < steps.size(); i++) {
            if (i == 0) {
                steps.get(i).setDropoffRate(0.0);
            } else {
                long currentCount = steps.get(i).getCount();
                long previousCount = steps.get(i - 1).getCount();
                double dropoff = ((double) (previousCount - currentCount) / previousCount) * 100;
                steps.get(i).setDropoffRate(dropoff);
            }
        }

        analysis.setSteps(steps);

        // Get completion stats
        FunnelCompletionStats completionStats = repository.aggregateFunnelCompletions(trackingId, funnelId);
        analysis.setCompletionStats(completionStats);

        // Calculate overall conversion rate
        if (!steps.isEmpty() && completionStats != null) {
            long firstStepCount = steps.get(0).getCount();
            long completions = completionStats.getCompletions();
            if (firstStepCount > 0) {
                analysis.setOverallConversionRate((double) completions / firstStepCount * 100);
            }
        }

        return analysis;
    }

    // ========== FORM ANALYTICS ==========

    public FormAnalyticsDTO getFormAnalytics(String trackingId, String formId) {
        FormAnalyticsDTO analytics = new FormAnalyticsDTO();
        analytics.setFormId(formId);

        // Get field interactions
        List<FormFieldStats> fieldStats = repository.aggregateFormFieldInteractions(trackingId, formId);
        analytics.setFieldStats(fieldStats);

        // Get abandonment stats
        List<FormAbandonmentStats> abandonmentStats = repository.aggregateFormAbandonments(trackingId, formId);
        analytics.setAbandonmentStats(abandonmentStats);

        // Calculate totals
        long totalSubmissions = repository.countByTrackingIdAndEventTypeAndTimestampBetween(
                trackingId, "form_submitted", Instant.EPOCH, Instant.now());
        long totalAbandonments = abandonmentStats.stream()
                .mapToLong(FormAbandonmentStats::getAbandonments).sum();

        analytics.setTotalSubmissions(totalSubmissions);
        analytics.setTotalAbandonments(totalAbandonments);

        // Calculate completion rate
        long totalAttempts = totalSubmissions + totalAbandonments;
        if (totalAttempts > 0) {
            analytics.setCompletionRate((double) totalSubmissions / totalAttempts * 100);
        }

        return analytics;
    }

    // ========== RAGE CLICK ANALYTICS ==========

    public List<RageClickStats> getRageClickStats(String trackingId, Instant from, Instant to) {
        return repository.aggregateRageClicks(trackingId, from, to);
    }

    public List<DeadClickStats> getDeadClickStats(String trackingId, Instant from, Instant to) {
        return repository.aggregateDeadClicks(trackingId, from, to);
    }

    // ========== HEATMAP DATA ==========

    public HeatmapDataDTO getHeatmapData(String trackingId, String url, Instant from, Instant to) {
        HeatmapDataDTO heatmap = new HeatmapDataDTO();
        heatmap.setUrl(url);

        // Get click data
        var clickEvents = repository.findHeatmapClicksForPage(trackingId, url, from, to);
        var clickPoints = clickEvents.stream()
                .map(event -> {
                    HeatmapPoint point = new HeatmapPoint();
                    point.setX(event.getClickX());
                    point.setY(event.getClickY());
                    point.setIntensity(1); // Aggregate in real implementation
                    return point;
                })
                .toList();
        heatmap.setClicks(clickPoints);

        // Get movement data (if needed)
        // var moveEvents = repository.findHeatmapMovementsForPage(trackingId, url,
        // from, to);
        // Process movement paths...

        return heatmap;
    }

    // ========== GENERAL ANALYTICS ==========

    public PerformanceMetricsDTO getPerformanceMetrics(String trackingId, Instant from, Instant to) {
        return repository.aggregatePerformanceMetrics(trackingId, from, to);
    }

    public List<EventTypeCount> getEventTypeCounts(String trackingId, Instant from, Instant to) {
        return repository.countEventsGroupedByType(trackingId, from, to);
    }

    public List<TimeSeriesPoint> getPageViewsTimeSeries(
            String trackingId, Instant from, Instant to, String unit) {
        return repository.aggregatePageViewsByTime(trackingId, from, to, unit);
    }

    public List<SessionViewDTO> getTopSessions(
            String trackingId, Instant from, Instant to, int limit) {
        return repository.aggregateSessionViewsPaginated(trackingId, from, to, 0, limit);
    }

    public List<ErrorSummaryDTO> getTopErrors(String trackingId, Instant from, Instant to) {
        return repository.aggregateTopErrors(trackingId, from, to);
    }
}
