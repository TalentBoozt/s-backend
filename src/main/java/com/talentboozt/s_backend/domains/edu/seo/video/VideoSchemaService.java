package com.talentboozt.s_backend.domains.edu.seo.video;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Technical Video SEO Schema Service.
 * Formulates standard Google-friendly VideoObject JSON-LD configurations complete
 * with search Seek-Clip offsets mapping to video lessons.
 */
@Service
public class VideoSchemaService {

    @Autowired
    private TranscriptChunkService transcriptChunkService;

    /**
     * Compiles detailed VideoObject schema models with dynamic Clip parameters.
     */
    public Map<String, Object> compileVideoSchema(
            String title, 
            String description, 
            String uploadDate, 
            String thumbnailUrl, 
            String embedUrl, 
            String rawTranscript) {
            
        Map<String, Object> schema = new HashMap<>();
        schema.put("@context", "https://schema.org");
        schema.put("@type", "VideoObject");
        schema.put("name", title);
        schema.put("description", description);
        schema.put("thumbnailUrl", thumbnailUrl);
        schema.put("uploadDate", uploadDate);
        schema.put("embedUrl", embedUrl);

        // Compile seek clip indices
        List<Map<String, String>> segments = transcriptChunkService.segmentTranscript(rawTranscript);
        List<Map<String, Object>> keyMoments = new ArrayList<>();
        
        for (int i = 0; i < segments.size(); i++) {
            Map<String, String> segment = segments.get(i);
            Map<String, Object> clip = new HashMap<>();
            clip.put("@type", "Clip");
            
            String text = segment.get("text");
            String clipLabel = "Moment " + (i + 1) + ": " + 
                              text.substring(0, Math.min(24, text.length())) + "...";
            
            clip.put("name", clipLabel);
            clip.put("startOffset", i * 120); // 120 seconds interval step
            keyMoments.add(clip);
        }
        
        schema.put("hasPart", keyMoments);
        return schema;
    }
}
