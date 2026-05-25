package com.talentboozt.s_backend.domains.edu.seo.sitemap;

import com.talentboozt.s_backend.domains.edu.model.ESkillNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class CategorySitemapProvider implements SeoUrlProvider {

    private static final String BASE_URL = "https://edu.talnova.io";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<SitemapUrl> getUrls() {
        List<SitemapUrl> urls = new ArrayList<>();
        String today = java.time.LocalDate.now().toString();

        try {
            List<ESkillNode> nodes = mongoTemplate.findAll(ESkillNode.class);
            for (ESkillNode node : nodes) {
                if (node.getSlug() != null) {
                    urls.add(new SitemapUrl(
                            BASE_URL + "/category/" + node.getSlug().toLowerCase(),
                            today,
                            "weekly",
                            0.7));
                }
            }
        } catch (Exception ignored) {
        }

        // Fallbacks based on top-level categories
        urls.add(new SitemapUrl(BASE_URL + "/explore?category=Development", today, "weekly", 0.8));
        urls.add(new SitemapUrl(BASE_URL + "/explore?category=Business", today, "weekly", 0.8));
        urls.add(new SitemapUrl(BASE_URL + "/explore?category=Design", today, "weekly", 0.8));
        urls.add(new SitemapUrl(BASE_URL + "/explore?category=Marketing", today, "weekly", 0.8));
        urls.add(new SitemapUrl(BASE_URL + "/explore?category=Strategy", today, "weekly", 0.8));
        urls.add(new SitemapUrl(BASE_URL + "/explore?category=Language", today, "weekly", 0.8));

        return urls;
    }
}
