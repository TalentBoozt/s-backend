package com.talentboozt.s_backend.domains.edu.seo;

import com.talentboozt.s_backend.domains.edu.seo.providers.SeoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SeoResolver {

    @Autowired
    private List<SeoProvider> providers;

    @Autowired
    private SeoFallbackProvider fallbackProvider;

    @Autowired
    private SeoSlugResolver slugResolver;

    @Autowired
    private CanonicalUrlService canonicalUrlService;

    public SeoMetadata resolveMetadata(String pageType, String slug, Map<String, Object> context) {
        String cleanSlug = slugResolver.resolveToSafeSlug(slug);
        SeoMetadata resolved = null;

        for (SeoProvider provider : providers) {
            if (provider.supports(pageType, context)) {
                try {
                    resolved = provider.generate(cleanSlug, context);
                    if (resolved != null) {
                        break;
                    }
                } catch (Exception ignored) {}
            }
        }

        if (resolved == null || isMetadataEmpty(resolved)) {
            // Apply contextual dynamic template fallback
            resolved = fallbackProvider.getFallback(cleanSlug, context);
        }

        // Enforce consistent dynamic URL standardisation and alternate lang elements
        resolved.setCanonicalUrl(canonicalUrlService.buildCanonicalUrl(cleanSlug, context));
        resolved.setHreflangs(canonicalUrlService.buildHreflangAlternateLinks(cleanSlug, context));

        return resolved;
    }

    private boolean isMetadataEmpty(SeoMetadata metadata) {
        return metadata.getTitle() == null || metadata.getTitle().isBlank() ||
               metadata.getDescription() == null || metadata.getDescription().isBlank();
    }
}
