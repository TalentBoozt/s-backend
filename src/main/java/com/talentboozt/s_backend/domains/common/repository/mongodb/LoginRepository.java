package com.talentboozt.s_backend.domains.common.repository.mongodb;

import com.talentboozt.s_backend.domains.sys_tracking.dto.monitor.LoginLocationAggregateDTO;
import com.talentboozt.s_backend.domains.common.model.Login;
import com.talentboozt.s_backend.domains.sys_tracking.dto.monitor.UniqueUserCountDTO;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoginRepository extends MongoRepository<Login, String> {
    Optional<Login> findByUserId(String userId);

    @Aggregation(pipeline = {
            "{ $unwind: '$events' }",
            "{ $match: { 'events.date': ?0 } }",
            "{ $group: { _id: '$userId' } }",
            "{ $count: 'uniqueUserCount' }"
    })
    UniqueUserCountDTO countDistinctUserIdByEventDate(String date);

    @Aggregation(pipeline = {
            "{ $unwind: '$events' }",
            "{ $unwind: '$events.metadata' }",
            "{ $group: { _id: '$events.metadata.platform', count: { $sum: 1 } } }"
    })
    Map<String, Long> countByPlatform(String trackingId);

    @Aggregation(pipeline = {
            "{ $unwind: '$events' }",
            "{ $unwind: '$events.metadata' }",
            "{ $match: { 'events.metadata.location.latitude': { $ne: null }, 'events.metadata.location.longitude': { $ne: null } } }",
            "{ $group: { _id: { lat: '$events.metadata.location.latitude', lng: '$events.metadata.location.longitude' }, value: { $sum: 1 } } }",
            "{ $project: { _id: 0, latitude: '$_id.lat', longitude: '$_id.lng', value: 1 } }"
    })
    List<LoginLocationAggregateDTO> aggregateLoginLocations(String trackingId, Instant from, Instant to);
}
