package com.talentboozt.s_backend.shared.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SitemapController {

    @GetMapping(value = "/sitemap.xml", produces = "application/xml")
    public String getSitemap() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">" +
                "<url>" +
                "<loc> https://talentboozt.com/</loc>" +
                "<changefreq>daily</changefreq>" +
                "<lastmod>2024-12-09T04:33:55+00:00</lastmod>" +
                "<priority>1.0</priority>" +
                "</url>" +
                "<url>" +
                "<loc>https://talentboozt.com/home</loc>" +
                "<changefreq>daily</changefreq>" +
                "<lastmod>2024-12-09T04:35:35+00:00</lastmod>" +
                "<priority>1.0</priority>" +
                "</url>" +
                "<url>" +
                "<loc>https://talentboozt.com/job</loc>" +
                "<changefreq>daily</changefreq>" +
                "<lastmod>2024-12-09T04:37:15+00:00</lastmod>" +
                "<priority>1.0</priority>" +
                "</url>" +
                "<url>" +
                "<loc>https://talentboozt.com/companies</loc>" +
                "<changefreq>daily</changefreq>" +
                "<lastmod>2024-12-09T04:40:20+00:00</lastmod>" +
                "<priority>1.0</priority>" +
                "</url>" +
                "<url>" +
                "<loc>https://talentboozt.com/about</loc>" +
                "<changefreq>daily</changefreq>" +
                "<lastmod>2024-12-09T04:42:38+00:00</lastmod>" +
                "<priority>1.0</priority>" +
                "</url>" +
                "<url>" +
                "<loc>https://talentboozt.com/pricing</loc>" +
                "<changefreq>daily</changefreq>" +
                "<lastmod>2024-12-09T04:44:42+00:00</lastmod>" +
                "<priority>1.0</priority>" +
                "</url>" +
                "<url>" +
                "<loc>https://talentboozt.com/for-companies</loc>" +
                "<changefreq>daily</changefreq>" +
                "<lastmod>2024-12-09T04:47:10+00:00</lastmod>" +
                "<priority>1.0</priority>" +
                "</url>" +
                "<url>" +
                "<loc>https://talentboozt.com/login</loc>" +
                "<changefreq>daily</changefreq>" +
                "<lastmod>2024-12-09T04:49:58+00:00</lastmod>" +
                "<priority>1.0</priority>" +
                "</url>" +
                "<url>" +
                "<loc>https://talentboozt.com/employees</loc>" +
                "<changefreq>daily</changefreq>" +
                "<lastmod>2024-12-09T04:52:59+00:00</lastmod>" +
                "<priority>1.0</priority>" +
                "</url>" +
                "<url>" +
                "<loc>https://talentboozt.com/terms-and-conditions</loc>" +
                "<changefreq>daily</changefreq>" +
                "<lastmod>2024-12-09T04:54:47+00:00</lastmod>" +
                "<priority>1.0</priority>" +
                "</url>" +
                "<url>" +
                "<loc>https://talentboozt.com/faq</loc>" +
                "<changefreq>daily</changefreq>" +
                "<lastmod>2024-12-09T04:55:25+00:00</lastmod>" +
                "<priority>1.0</priority>" +
                "</url>" +
                "<url>" +
                "<loc>https://talentboozt.com/contact</loc>" +
                "<changefreq>daily</changefreq>" +
                "<lastmod>2024-12-09T04:57:09+00:00</lastmod>" +
                "<priority>1.0</priority>" +
                "</url>" +
                "</urlset>";
    }
}

