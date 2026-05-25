package com.talentboozt.s_backend.domains.edu.seo.sitemap;

import com.talentboozt.s_backend.domains.edu.model.EProgrammaticPage;
import com.talentboozt.s_backend.domains.edu.programmatic.ProgrammaticPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class MaterialSitemapProvider implements SeoUrlProvider {

    private static final String BASE_URL = "https://edu.talnova.io";

    @Autowired
    private ProgrammaticPageRepository programmaticRepository;

    @Override
    public List<SitemapUrl> getUrls() {
        List<SitemapUrl> urls = new ArrayList<>();
        List<EProgrammaticPage> pages = programmaticRepository.findAll();
        String today = java.time.LocalDate.now().toString();

        for (EProgrammaticPage page : pages) {
            urls.add(new SitemapUrl(
                BASE_URL + "/materials/" + page.getSlug().toLowerCase(),
                today,
                "weekly",
                0.8
            ));
        }

        if (urls.isEmpty()) {
            urls.add(new SitemapUrl(
                BASE_URL + "/materials/chatgpt-prompt-engineering-guide",
                today,
                "weekly",
                0.8
            ));
        }

        return urls;
    }
}
