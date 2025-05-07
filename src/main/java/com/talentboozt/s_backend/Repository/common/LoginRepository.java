package com.talentboozt.s_backend.Repository.common;

import com.talentboozt.s_backend.DTO.SYS_TRACKING.monitor.LoginLocationAggregateDTO;
import com.talentboozt.s_backend.Model.common.Login;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LoginRepository extends MongoRepository<Login, String> {
    Optional<Login> findByUserId(String userId);

    long countDistinctUserIdByLoginDatesContaining(String date);

    @Aggregation(pipeline = {
            "{ $unwind: '$metaData' }",
            "{ $group: { _id: '$metaData.platform', count: { $sum: 1 } } }"
    })
    Map<String, Long> countByPlatform(String trackingId);

    @Aggregation(pipeline = {
            "{ $unwind: '$metaData' }",
            "{ $match: { 'metaData.location.latitude': { $ne: null }, 'metaData.location.longitude': { $ne: null } } }",
            "{ $group: { _id: { lat: '$metaData.location.latitude', lng: '$metaData.location.longitude' }, value: { $sum: 1 } } }",
            "{ $project: { _id: 0, latitude: '$_id.lat', longitude: '$_id.lng', value: 1 } }"
    })
    List<LoginLocationAggregateDTO> aggregateLoginLocations(String trackingId, Instant from, Instant to);
}
