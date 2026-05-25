package com.talentboozt.s_backend.domains.edu.seo.sitemap;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StaticPageSitemapProvider implements SeoUrlProvider {

    private static final String BASE_URL = "https://edu.talnova.io";

    @Override
    public List<SitemapUrl> getUrls() {
        List<SitemapUrl> urls = new ArrayList<>();
        String today = java.time.LocalDate.now().toString();

        urls.add(new SitemapUrl(BASE_URL + "/", today, "daily", 1.0));
        urls.add(new SitemapUrl(BASE_URL + "/explore", today, "daily", 0.9));
        urls.add(new SitemapUrl(BASE_URL + "/pricing", today, "weekly", 0.8));
        urls.add(new SitemapUrl(BASE_URL + "/faq", today, "monthly", 0.6));
        urls.add(new SitemapUrl(BASE_URL + "/careers", today, "daily", 0.9));
        urls.add(new SitemapUrl(BASE_URL + "/ai-courses", today, "daily", 0.9));
        urls.add(new SitemapUrl(BASE_URL + "/freelancing", today, "daily", 0.9));
        urls.add(new SitemapUrl(BASE_URL + "/exam-preparation", today, "daily", 0.8));

        return urls;
    }
}
