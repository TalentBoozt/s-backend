package com.talentboozt.s_backend.Repository.SYS_TRACKING;

import com.talentboozt.s_backend.DTO.SYS_TRACKING.monitor.*;
import com.talentboozt.s_backend.Model.SYS_TRACKING.TrackingEvent;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface TrackingEventRepository extends MongoRepository<TrackingEvent, String> {
    List<TrackingEvent> findByTrackingId(String trackingId);

    long countDistinctSessionIdByTrackingIdAndTimestampBetween(String trackingId, Instant from, Instant to);

    long countByTrackingIdAndErrorMessageNotNullAndTimestampBetween(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 }, eventType: 'page_view' } }",
            "{ $group: { _id: { $dateTrunc: { date: '$timestamp', unit: 'hour' } }, count: { $sum: 1 } } }",
            "{ $project: { timestamp: '$_id', count: 1, _id: 0 } }",
            "{ $sort: { timestamp: 1 } }"
    })
    List<TimeSeriesPoint> aggregatePageViewsByTime(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 }, eventType: 'click' } }",
            "{ $group: { _id: { $dateTrunc: { date: '$timestamp', unit: 'hour' } }, count: { $sum: 1 } } }",
            "{ $project: { timestamp: '$_id', count: 1, _id: 0 } }",
            "{ $sort: { timestamp: 1 } }"
    })
    List<TimeSeriesPoint> aggregatePageClicksByTime(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 }, eventType: 'page_performance' } }",
            "{ $group: { _id: { $dateTrunc: { date: '$timestamp', unit: 'hour' } }, count: { $sum: 1 } } }",
            "{ $project: { timestamp: '$_id', count: 1, _id: 0 } }",
            "{ $sort: { timestamp: 1 } }"
    })
    List<TimeSeriesPoint> aggregatePagePerformanceByTime(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: '$eventType', count: { $sum: 1 } } }",
            "{ $project: { eventType: '$_id', count: 1, _id: 0 } }"
    })
    List<EventTypeCount> countEventsGroupedByType(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: null, avgDomLoadTime: { $avg: '$domLoadTime' }, avgFullLoadTime: { $avg: '$fullLoadTime' }, avgTtfb: { $avg: '$ttfb' } } }"
    })
    PerformanceMetricsDTO aggregatePerformanceMetrics(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: '$sessionId', userId: { $first: '$userId' }, urls: { $push: '$url' }, duration: { $sum: '$durationMs' }, eventCount: { $sum: 1 } } }",
            "{ $project: { sessionId: '$_id', userId: 1, urls: 1, duration: 1, eventCount: 1, _id: 0 } }",
            "{ $sort: { duration: -1 } }",
            "{ $limit: 10 }"
    })
    List<SessionViewDTO> aggregateSessionViews(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: { country: '$country', city: '$city' }, count: { $sum: 1 } } }"
    })
    List<LocationCountDTO> aggregateByLocation(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0 } }",
            "{ $group: { _id: '$browser', count: { $sum: 1 } } }"
    })
    Map<String, Long> countByBrowser(String trackingId);

    List<TrackingEvent> findByTrackingIdAndTimestampBetween(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, timestamp: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: '$screenResolution', count: { $sum: 1 } } }",
            "{ $project: { resolution: '$_id', count: 1, _id: 0 } }"
    })
    List<ScreenResolutionCount> countByScreenResolution(String trackingId, Instant from, Instant to);

    @Aggregation(pipeline = {
            "{ $match: { trackingId: ?0, sessionId: ?1 } }",
            "{ $sort: { timestamp: 1 } }"
    })
    List<TrackingEvent> findSessionEvents(String trackingId, String sessionId);

}
