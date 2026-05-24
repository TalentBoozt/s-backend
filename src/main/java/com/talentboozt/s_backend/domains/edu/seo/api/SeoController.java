package com.talentboozt.s_backend.domains.edu.seo.api;

import com.talentboozt.s_backend.domains.edu.seo.ai.application.SeoAiContentService;
import com.talentboozt.s_backend.domains.edu.seo.geo.application.SeoGeoService;
import com.talentboozt.s_backend.domains.edu.seo.monitoring.application.SeoMonitoringService;
import com.talentboozt.s_backend.domains.edu.seo.programmatic.application.ProgrammaticContentEngine;
import com.talentboozt.s_backend.domains.edu.seo.authority.application.AiContentGenerationService;
import com.talentboozt.s_backend.domains.edu.seo.indexing.application.InternalLinkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/edu/seo")
public class SeoController {

    @Autowired
    private SeoAiContentService seoAiContentService;

    @Autowired
    private SeoGeoService seoGeoService;

    @Autowired
    private SeoMonitoringService seoMonitoringService;

    @Autowired
    private ProgrammaticContentEngine programmaticContentEngine;

    @Autowired
    private AiContentGenerationService aiContentGenerationService;

    @Autowired
    private InternalLinkingService internalLinkingService;

    @PostMapping("/ai/snippet")
    public Map<String, Object> snippet(@RequestParam String query, @RequestParam String content) {
        return seoAiContentService.compileConversationalSnippet(query, content);
    }

    @PostMapping("/geo/intent")
    public Map<String, Object> geo(@RequestParam String location, @RequestParam String course) {
        return seoGeoService.mapGeoIntent(location, course);
    }

    @GetMapping("/monitor")
    public Map<String, Object> monitor(@RequestParam String url) {
        return seoMonitoringService.evaluateSeoHealth(url);
    }

    @PostMapping("/programmatic/page")
    public Map<String, Object> programmatic(@RequestParam String role) {
        return programmaticContentEngine.compileProgrammaticPage(role);
    }

    @PostMapping("/authority/faq")
    public Map<String, Object> faq(@RequestParam String title) {
        return aiContentGenerationService.enrichStudyContent(title);
    }

    @GetMapping("/indexing/links")
    public List<Map<String, Object>> links(@RequestParam String role) {
        return internalLinkingService.mapInternalLinks(role);
    }
}
