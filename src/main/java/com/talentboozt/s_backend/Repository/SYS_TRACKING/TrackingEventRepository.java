package com.talentboozt.s_backend.Repository.SYS_TRACKING;

import com.talentboozt.s_backend.Model.SYS_TRACKING.TrackingEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TrackingEventRepository extends MongoRepository<TrackingEvent, String> {
    List<TrackingEvent> findByTrackingId(String trackingId);
}
