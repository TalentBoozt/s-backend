package com.talentboozt.s_backend.domains.edu.community.application;

import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AppPeerLearningService {

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
