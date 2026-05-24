package com.talentboozt.s_backend.domains.edu.mobile;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Mobile-First Offline Caching and Bandwidth Optimization Service.
 * Evaluates streaming profiles (low data versus high resolution) and downloadable
 * filesizes to ensure performance on low-bandwidth setups.
 */
@Service
public class OfflineLearningCacheService {

    /**
     * Determines offline caching metadata based on user cellular profiles.
     */
    public Map<String, Object> compileCacheMetadata(String lessonId, String bandwidthProfileType) {
        Map<String, Object> cacheMap = new HashMap<>();
        cacheMap.put("lessonId", lessonId);
        
        String targetQuality = (bandwidthProfileType != null && bandwidthProfileType.toLowerCase().contains("low")) ? 
                               "360p_LOW_DATA_SAVER" : "1080p_HIGH_QUALITY_FULL";
                               
        cacheMap.put("videoQualityProfile", targetQuality);
        cacheMap.put("downloadSizeMb", targetQuality.contains("360") ? 12.5 : 95.0);
        cacheMap.put("downloadStatus", "METADATA_CACHED_READY");
        
        return cacheMap;
    }
}
