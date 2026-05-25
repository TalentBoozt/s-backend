package com.talentboozt.s_backend.domains.edu.seo.sitemap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SitemapService {

    @Autowired
    private SitemapCache sitemapCache;

    @Autowired
    private SitemapGenerator sitemapGenerator;

    @Autowired
    private SitemapIndexGenerator sitemapIndexGenerator;

    @Autowired
    private CourseSitemapProvider courseProvider;

    @Autowired
    private MaterialSitemapProvider materialProvider;

    @Autowired
    private CategorySitemapProvider categoryProvider;

    @Autowired
    private StaticPageSitemapProvider staticProvider;

    public String getSitemapIndex() {
        String cacheKey = "sitemap_index";
        String cachedXml = sitemapCache.get(cacheKey);
        if (cachedXml != null) {
            return cachedXml;
        }

        List<String> files = List.of(
            "sitemap_courses.xml",
            "sitemap_materials.xml",
            "sitemap_categories.xml",
            "sitemap_static.xml"
        );
        String indexXml = sitemapIndexGenerator.generateIndexXml(files);
        sitemapCache.put(cacheKey, indexXml);
        return indexXml;
    }

    public String getSitemap(String type) {
        String cacheKey = "sitemap_" + type;
        String cachedXml = sitemapCache.get(cacheKey);
        if (cachedXml != null) {
            return cachedXml;
        }

        List<SitemapUrl> urls;
        switch (type.toLowerCase()) {
            case "courses":
                urls = courseProvider.getUrls();
                break;
            case "materials":
                urls = materialProvider.getUrls();
                break;
            case "categories":
                urls = categoryProvider.getUrls();
                break;
            case "static":
            default:
                urls = staticProvider.getUrls();
                break;
        }

        String xml = sitemapGenerator.generateXml(urls);
        sitemapCache.put(cacheKey, xml);
        return xml;
    }

    public void evictCache() {
        sitemapCache.evictAll();
    }
}
