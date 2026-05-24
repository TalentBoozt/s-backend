package com.talentboozt.s_backend.domains.edu.community;

import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Peer-to-Peer Study Circle and Accountability Service.
 * Matches students studying similar skills into study circles to optimize retention.
 */
@Service
public class PeerLearningService {

    /**
     * Initializes a peer study circle.
     */
    public Map<String, Object> createStudyCircle(String circleName, String targetedSubject, List<String> userIdsList) {
        Map<String, Object> studyCircleMap = new HashMap<>();
        
        studyCircleMap.put("circleId", UUID.randomUUID().toString());
        studyCircleMap.put("name", circleName);
        studyCircleMap.put("subject", targetedSubject);
        studyCircleMap.put("members", userIdsList);
        studyCircleMap.put("createdDate", new Date());
        
        return studyCircleMap;
    }
}
