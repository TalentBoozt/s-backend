package com.talentboozt.s_backend.domains.community.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.talentboozt.s_backend.domains.community.dto.PostDTO;

import java.net.URI;

@Service
public class LinkPreviewService {

    @Cacheable(value = "linkPreviews", unless = "#result == null")
    public PostDTO.LinkPreviewDTO getPreview(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(5000)
                    .get();

            String title = getMetaTag(doc, "og:title");
            if (title == null)
                title = doc.title();

            String description = getMetaTag(doc, "og:description");
            if (description == null)
                description = getMetaTag(doc, "description");

            String image = getMetaTag(doc, "og:image");

            String siteName = getMetaTag(doc, "og:site_name");
            if (siteName == null) {
                try {
                    URI uri = new URI(url);
                    siteName = uri.getHost();
                    if (siteName != null && siteName.startsWith("www.")) {
                        siteName = siteName.substring(4);
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }

            return PostDTO.LinkPreviewDTO.builder()
                    .title(title)
                    .description(description)
                    .image(image)
                    .siteName(siteName)
                    .build();

        } catch (Exception e) {
            return null;
        }
    }

    private String getMetaTag(Document doc, String attr) {
        try {
            return doc.select("meta[property=" + attr + "]").attr("content");
        } catch (Exception e) {
            try {
                return doc.select("meta[name=" + attr + "]").attr("content");
            } catch (Exception e2) {
                return null;
            }
        }
    }
}
