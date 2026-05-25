package com.talentboozt.s_backend.domains.edu.seo;

import com.talentboozt.s_backend.domains.edu.seo.dto.HreflangEntryDto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicSeoFactory {
    
    private static final String BASE_URL = "https://edu.talnova.io";

    public static SeoMetadata createBaseSeo(String path, String title, String description, String keywords) {
        String cleanPath = (path != null && path.startsWith("/")) ? path : "/" + path;
        if (cleanPath.equals("/")) cleanPath = "";
        
        String canonical = BASE_URL + cleanPath;
        
        List<HreflangEntryDto> hreflangs = List.of(
            new HreflangEntryDto("en-LK", canonical),
            new HreflangEntryDto("si-LK", BASE_URL + "/si-LK" + cleanPath),
            new HreflangEntryDto("ta-LK", BASE_URL + "/ta-LK" + cleanPath),
            new HreflangEntryDto("x-default", canonical)
        );

        Map<String, String> openGraph = new HashMap<>();
        openGraph.put("og:title", title);
        openGraph.put("og:description", description);
        openGraph.put("og:url", canonical);
        openGraph.put("og:type", "website");
        openGraph.put("og:image", BASE_URL + "/default-og.png");

        Map<String, String> twitterCard = new HashMap<>();
        twitterCard.put("twitter:card", "summary_large_image");
        twitterCard.put("twitter:title", title);
        twitterCard.put("twitter:description", description);
        twitterCard.put("twitter:image", BASE_URL + "/default-og.png");

        return SeoMetadata.builder()
                .title(title)
                .description(description)
                .canonicalUrl(canonical)
                .hreflangs(new ArrayList<>(hreflangs))
                .openGraph(openGraph)
                .twitterCard(twitterCard)
                .keywords(keywords)
                .robotsDirectives("index, follow")
                .lang("en-LK")
                .schemaJsonLd("{}")
                .build();
    }
}
