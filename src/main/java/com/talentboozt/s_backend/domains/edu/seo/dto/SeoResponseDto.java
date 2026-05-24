package com.talentboozt.s_backend.domains.edu.seo.dto;

import java.util.List;
import java.util.Map;

/**
 * Enterprise SEO Metadata Response Object.
 * Combines all standard crawling variables, schemas, canonical targets, Open Graph mappings,
 * and robots indexing rules.
 */
public record SeoResponseDto(
    String title,
    String description,
    String canonical,
    List<HreflangEntryDto> hreflangs,
    Map<String, String> openGraph,
    String schema,
    String robots,
    String keywords
) {}
