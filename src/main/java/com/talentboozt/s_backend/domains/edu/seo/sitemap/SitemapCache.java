package com.talentboozt.s_backend.domains.edu.seo.sitemap;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SitemapCache {

    private final Map<String, String> sitemapXmlCache = new ConcurrentHashMap<>();

    public String get(String key) {
        return sitemapXmlCache.get(key);
    }

    public void put(String key, String xml) {
        sitemapXmlCache.put(key, xml);
    }

    public void evictAll() {
        sitemapXmlCache.clear();
        System.out.println("[SitemapCache] Dynamic Sitemap XML cache evicted successfully.");
    }
}
