package com.talentboozt.s_backend.domains.sys_tracking.repository;

import com.talentboozt.s_backend.domains.sys_tracking.dto.monitor.*;
import com.talentboozt.s_backend.domains.sys_tracking.model.TrackingEvent;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Enhanced Repository for v2.0
 * Includes queries for A/B testing, funnels, forms, and heatmaps
 */
@Repository
public interface TrackingEventRepository extends MongoRepository<TrackingEvent, String> {

    // ========== BASIC QUERIES ==========

    List<TrackingEvent> findByTrackingId(String trackingId);

    List<TrackingEvent> findByTrackingIdAndTimestampBetween(String trackingId, Instant from, Instant to);

    List<TrackingEvent> findByTrackingIdAndEventTypeAndTimestampBetween(
            String trackingId, String eventType, Instant from, Instant to);

    // ========== SESSION ANALYTICS ==========

    long countDistinctSessionIdByTrackingIdAndTimestampBetween(
            String trackingId, Instant from, Instant to);

    long countByTrackingIdAndEventTypeAndTimestampBetween(String trackingId, String eventType, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, sessionId: ?1 } }",
            "{ $sort: { timestamp: 1 } }"
    })
    List<TrackingEvent> findSessionEvents(String trackingId, String sessionId);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: '$sessionId', userId: { $first: '$userId' }, " +
                    "  urls: { $push: '$url' }, duration: { $sum: '$durationMs' }, " +
                    "  eventCount: { $sum: 1 } } }",
            "{ $project: { sessionId: '$_id', userId: 1, urls: 1, duration: 1, eventCount: 1, _id: 0 } }",
            "{ $sort: { duration: -1 } }",
            "{ $skip: ?3 }",
            "{ $limit: ?4 }"
    })
    List<SessionViewDTO> aggregateSessionViewsPaginated(
            String trackingId, Instant from, Instant to, int offset, int limit);

    // ========== EVENT TYPE ANALYTICS ==========

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: '$eventType', count: { $sum: 1 } } }",
            "{ $project: { eventType: '$_id', count: 1, _id: 0 } }"
    })
    List<EventTypeCount> countEventsGroupedByType(String trackingId, Instant from, Instant to);

    // ========== TIME SERIES ==========

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 }, eventType: 'page_view' } }",
            "{ $group: { _id: { $dateTrunc: { date: '$timestamp', unit: ?3 } }, count: { $sum: 1 } } }",
            "{ $project: { timestamp: '$_id', count: 1, _id: 0 } }",
            "{ $sort: { timestamp: 1 } }"
    })
    List<TimeSeriesPoint> aggregatePageViewsByTime(
            String trackingId, Instant from, Instant to, String unit);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 }, eventType: 'click' } }",
            "{ $group: { _id: { $dateTrunc: { date: '$timestamp', unit: ?3 } }, count: { $sum: 1 } } }",
            "{ $project: { timestamp: '$_id', count: 1, _id: 0 } }",
            "{ $sort: { timestamp: 1 } }"
    })
    List<TimeSeriesPoint> aggregatePageClicksByTime(
            String trackingId, Instant from, Instant to, String unit);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 }, eventType: 'page_performance' } }",
            "{ $group: { _id: { $dateTrunc: { date: '$timestamp', unit: ?3 } }, count: { $sum: 1 } } }",
            "{ $project: { timestamp: '$_id', count: 1, _id: 0 } }",
            "{ $sort: { timestamp: 1 } }"
    })
    List<TimeSeriesPoint> aggregatePagePerformanceByTime(String trackingId, Instant from, Instant to, String unit);

    // ========== PERFORMANCE METRICS ==========

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 }, eventType: 'page_performance' } }",
            "{ $group: { _id: null, " +
                    "  avgDomLoadTime: { $avg: '$domLoadTime' }, " +
                    "  avgFullLoadTime: { $avg: '$fullLoadTime' }, " +
                    "  avgTtfb: { $avg: '$ttfb' }, " +
                    "  avgDnsTime: { $avg: '$dnsTime' }, " +
                    "  avgTcpTime: { $avg: '$tcpTime' }, " +
                    "  avgDownloadTime: { $avg: '$downloadTime' } } }"
    })
    PerformanceMetricsDTO aggregatePerformanceMetrics(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 }, eventType: { $in: ['web_vital_lcp', 'web_vital_fid'] } } }",
            "{ $group: { _id: '$eventType', avgValue: { $avg: '$value' }, p75: { $percentile: { input: '$value', p: [0.75], method: 'approximate' } } } }"
    })
    List<WebVitalsDTO> aggregateWebVitals(String trackingId, Instant from, Instant to);

    // ========== GEO ANALYTICS ==========

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: { country: '$country', city: '$city' }, count: { $sum: 1 } } }"
    })
    List<LocationCountDTO> aggregateByLocation(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: '$screenResolution', count: { $sum: 1 } } }",
            "{ $project: { resolution: '$_id', count: 1, _id: 0 } }"
    })
    List<ScreenResolutionCount> countByScreenResolution(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0 } }",
            "{ $group: { _id: '$browser', count: { $sum: 1 } } }"
    })
    Map<String, Long> countByBrowser(String trackingId);

    // ========== ERROR TRACKING ==========

    long countByTrackingIdAndErrorMessageNotNullAndTimestampBetween(
            String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 }, errorMessage: { $ne: null } } }",
            "{ $group: { _id: '$errorMessage', count: { $sum: 1 }, lastOccurred: { $max: '$timestamp' } } }",
            "{ $sort: { count: -1 } }",
            "{ $limit: 20 }"
    })
    List<ErrorSummaryDTO> aggregateTopErrors(String trackingId, Instant from, Instant to);

    // ========== A/B TESTING / EXPERIMENTS ==========

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, experimentId: ?1, eventType: 'experiment_assigned' } }",
            "{ $group: { _id: '$variant', count: { $sum: 1 } } }",
            "{ $project: { variant: '$_id', count: 1, _id: 0 } }"
    })
    List<ExperimentVariantCount> countExperimentAssignments(String trackingId, String experimentId);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, experimentId: ?1, eventType: 'experiment_conversion' } }",
            "{ $group: { _id: '$variant', " +
                    "  conversions: { $sum: 1 }, " +
                    "  totalValue: { $sum: '$conversionValue' }, " +
                    "  avgValue: { $avg: '$conversionValue' } } }",
            "{ $project: { variant: '$_id', conversions: 1, totalValue: 1, avgValue: 1, _id: 0 } }"
    })
    List<ExperimentConversionStats> aggregateExperimentConversions(
            String trackingId, String experimentId);

    // ========== FUNNEL ANALYTICS ==========

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, funnelId: ?1, eventType: 'funnel_step' } }",
            "{ $group: { _id: { stepIndex: '$stepIndex', stepName: '$stepName' }, count: { $sum: 1 } } }",
            "{ $project: { stepIndex: '$_id.stepIndex', stepName: '$_id.stepName', count: 1, _id: 0 } }",
            "{ $sort: { stepIndex: 1 } }"
    })
    List<FunnelStepStats> aggregateFunnelSteps(String trackingId, String funnelId);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, funnelId: ?1, eventType: 'funnel_complete' } }",
            "{ $group: { _id: null, " +
                    "  completions: { $sum: 1 }, " +
                    "  avgDuration: { $avg: '$durationMs' } } }"
    })
    FunnelCompletionStats aggregateFunnelCompletions(String trackingId, String funnelId);

    // ========== FORM ANALYTICS ==========

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, formId: ?1, eventType: 'form_field_focus' } }",
            "{ $group: { _id: '$fieldName', interactions: { $sum: 1 }, avgTimeSpent: { $avg: '$timeSpentMs' } } }",
            "{ $project: { fieldName: '$_id', interactions: 1, avgTimeSpent: 1, _id: 0 } }"
    })
    List<FormFieldStats> aggregateFormFieldInteractions(String trackingId, String formId);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, formId: ?1, eventType: 'form_abandoned' } }",
            "{ $group: { _id: '$fieldName', abandonments: { $sum: 1 } } }",
            "{ $project: { fieldName: '$_id', abandonments: 1, _id: 0 } }",
            "{ $sort: { abandonments: -1 } }"
    })
    List<FormAbandonmentStats> aggregateFormAbandonments(String trackingId, String formId);

    // ========== RAGE CLICKS & DEAD CLICKS ==========

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, eventType: 'rage_click', timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: { elementType: '$elementType', elementText: '$elementText' }, " +
                    "  count: { $sum: 1 }, avgClicks: { $avg: '$clickCount' } } }",
            "{ $sort: { count: -1 } }",
            "{ $limit: 20 }"
    })
    List<RageClickStats> aggregateRageClicks(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, eventType: 'dead_click', timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: { elementType: '$elementType', elementText: '$elementText' }, count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }",
            "{ $limit: 20 }"
    })
    List<DeadClickStats> aggregateDeadClicks(String trackingId, Instant from, Instant to);

    // ========== HEATMAP DATA ==========

    @Query("{ 'trackingId': ?0, 'eventType': 'heatmap_click', 'url': ?1, 'timestamp': { $gte: ?2, $lte: ?3 } }")
    List<TrackingEvent> findHeatmapClicksForPage(
            String trackingId, String url, Instant from, Instant to);

    @Query("{ 'trackingId': ?0, 'eventType': 'heatmap_move', 'url': ?1, 'timestamp': { $gte: ?2, $lte: ?3 } }")
    List<TrackingEvent> findHeatmapMovementsForPage(
            String trackingId, String url, Instant from, Instant to);
}
