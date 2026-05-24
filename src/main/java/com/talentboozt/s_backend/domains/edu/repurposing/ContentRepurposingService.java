package com.talentboozt.s_backend.domains.edu.repurposing;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Organic Content Marketing and Repurposing Service.
 * Repurposes transcripts into TikTok/shorts video hooks, LinkedIn updates,
 * and email highlights automatically.
 */
@Service
public class ContentRepurposingService {

    /**
     * Converts lesson summaries to targeted social marketing copy formats.
     */
    public Map<String, String> repurposeLessonContent(String lessonTitle, String lessonContextBrief) {
        Map<String, String> repurposedOutputs = new HashMap<>();
        
        repurposedOutputs.put("blogDraft", "## Expert Guide: Mastering " + lessonTitle + "\n\n" + 
                                           lessonContextBrief + "\n\nJoin Talnova to build portfolio-grade skills.");
                                           
        repurposedOutputs.put("tiktokHook", "Think you know all about " + lessonTitle + 
                                            "? Here is the absolute easiest way to master it in 30 seconds!");
                                            
        repurposedOutputs.put("linkedinPost", "💡 Learning " + lessonTitle + 
                                              " is a game-changer for digital careers. Here is a quick 3-step breakdown of how to master it...");
                                              
        repurposedOutputs.put("emailSummary", "Hi Explorer,\n\nWe just compiled a brand new revision guide on " + 
                                              lessonTitle + ". Click here to check it out.");
        
        return repurposedOutputs;
    }
}
