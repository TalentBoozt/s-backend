package com.talentboozt.s_backend.domains.edu.seo.sitemap;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.seo.repository.CourseSeoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class CourseSitemapProvider implements SeoUrlProvider {

    private static final String BASE_URL = "https://edu.talnova.io";

    @Autowired
    private CourseSeoRepository courseRepository;

    @Override
    public List<SitemapUrl> getUrls() {
        List<SitemapUrl> urls = new ArrayList<>();
        List<ECourses> courses = courseRepository.findAllIndexableProjections();
        String today = java.time.LocalDate.now().toString();

        for (ECourses course : courses) {
            String lastmod = course.getUpdatedAt() != null 
                    ? course.getUpdatedAt().toString().substring(0, 10) 
                    : today;
            urls.add(new SitemapUrl(
                BASE_URL + "/course/" + course.getSeoSlug().toLowerCase(),
                lastmod,
                "daily",
                0.9
            ));
        }

        if (urls.isEmpty()) {
            urls.add(new SitemapUrl(
                BASE_URL + "/course/al-physics-theory",
                today,
                "daily",
                0.9
            ));
        }

        return urls;
    }
}
