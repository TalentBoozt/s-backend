package com.talentboozt.s_backend.domains.edu.seo.video;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Educational Video Transcript Chunking Service.
 * Partitions lecture transcript documents into timestamp-referenced paragraphs
 * to feed video semantic Search crawlers and indexing clips schemas.
 */
@Service
public class TranscriptChunkService {

    /**
     * Splits raw lecture transcript inputs into timestamp blocks.
     */
    public List<Map<String, String>> segmentTranscript(String rawTranscript) {
        List<Map<String, String>> segments = new ArrayList<>();
        if (rawTranscript == null || rawTranscript.isEmpty()) return segments;

        String[] sentences = rawTranscript.split("\\.");
        int sentenceCounter = 0;
        StringBuilder currentChunk = new StringBuilder();

        for (String sentence : sentences) {
            currentChunk.append(sentence.trim()).append(". ");
            sentenceCounter++;
            
            if (sentenceCounter >= 3) {
                Map<String, String> segment = new HashMap<>();
                int minutes = segments.size() * 2;
                segment.put("timestamp", String.format("00:%02d:00", minutes));
                segment.put("text", currentChunk.toString().trim());
                segments.add(segment);
                
                currentChunk = new StringBuilder();
                sentenceCounter = 0;
            }
        }

        if (currentChunk.length() > 0) {
            Map<String, String> segment = new HashMap<>();
            int minutes = segments.size() * 2;
            segment.put("timestamp", String.format("00:%02d:00", minutes));
            segment.put("text", currentChunk.toString().trim());
            segments.add(segment);
        }

        return segments;
    }
}
