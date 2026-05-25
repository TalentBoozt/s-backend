package com.talentboozt.s_backend.domains.edu.seo.providers;

import com.talentboozt.s_backend.domains.edu.seo.SeoMetadata;
import java.util.Map;

public interface SeoProvider {
    boolean supports(String pageType, Map<String, Object> context);
    SeoMetadata generate(String slug, Map<String, Object> context);
}
