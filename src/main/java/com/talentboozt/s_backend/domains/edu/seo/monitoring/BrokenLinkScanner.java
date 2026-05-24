package com.talentboozt.s_backend.domains.edu.seo.monitoring;

import org.springframework.stereotype.Service;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Organic Broken Link Scanner.
 * Automatically runs quick HTTP HEAD requests on links to catch broken 404 pages.
 */
@Service
public class BrokenLinkScanner {

    /**
     * Probes an absolute link and checks for valid responses.
     */
    public boolean testLinkUrl(String linkUrl) {
        if (linkUrl == null || !linkUrl.startsWith("http")) return false;
        
        try {
            URL url = new URL(linkUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(2500);
            connection.setReadTimeout(2500);
            int code = connection.getResponseCode();
            return code < 400;
        } catch (Exception e) {
            return false;
        }
    }
}
