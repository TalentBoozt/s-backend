package com.talentboozt.s_backend.domains.edu.seo.dto;

/**
 * Immutable Data Transfer Object mapping alternate language links for SEO response headers.
 */
public record HreflangEntryDto(
    String lang,
    String href
) {}
