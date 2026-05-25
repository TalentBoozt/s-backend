package com.talentboozt.s_backend.domains.edu.seo;

import com.talentboozt.s_backend.domains.edu.seo.dto.HreflangEntryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoMetadata {
    private String title;
    private String description;
    private String canonicalUrl;
    
    @Builder.Default
    private List<HreflangEntryDto> hreflangs = new ArrayList<>();
    
    @Builder.Default
    private Map<String, String> openGraph = new HashMap<>();
    
    @Builder.Default
    private Map<String, String> twitterCard = new HashMap<>();
    
    private String schemaJsonLd;
    private String keywords;
    private String robotsDirectives;
    private String lang;
}
