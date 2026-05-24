package com.talentboozt.s_backend.domains.edu.seo.authority.application;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiContentGenerationService {

    public Map<String, Object> enrichStudyContent(String syllabusTitle) {
        Map<String, Object> faq = new HashMap<>();
        faq.put("syllabusArea", syllabusTitle);
        faq.put("expandedFaqs", List.of(
            "What are structural requirements for " + syllabusTitle + "?",
            "Why is " + syllabusTitle + " critical for remote careers?"
        ));
        faq.put("jsonSchemaExpansion", "{\"@context\":\"https://schema.org\",\"@type\":\"FAQPage\"}");
        return faq;
    }
}
