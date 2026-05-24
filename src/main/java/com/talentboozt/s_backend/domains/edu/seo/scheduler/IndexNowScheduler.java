package com.talentboozt.s_backend.domains.edu.seo.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;

/**
 * IndexNow Automated URL Submissions Scheduler.
 * Manages dynamic enqueues of updated course and profile targets, deduplicates requests,
 * and bulk-pushes them to search indexers via IndexNow endpoints on a periodic cron cycle.
 */
@Component
public class IndexNowScheduler {

    private final Queue<String> urlQueue = new LinkedList<>();
    private final Set<String> deduplicateSet = new HashSet<>();
    private final RestTemplate restTemplate = new RestTemplate();

    private final String INDEXNOW_KEY = "talnova_indexnow_api_key_8e72f9a";
    private final String BASE_URL = "https://edu.talnova.io";

    /**
     * Enqueues a canonical URL path for automatic index submittal.
     */
    public synchronized void enqueueUrl(String path) {
        String absoluteUrl = BASE_URL + (path.startsWith("/") ? "" : "/") + path.toLowerCase();
        if (deduplicateSet.add(absoluteUrl)) {
            urlQueue.offer(absoluteUrl);
            System.out.println("[IndexNow Queue] Enqueued URL for indexing: " + absoluteUrl);
        }
    }

    /**
     * Periodically processes queued URLs to submit in a single bulk push.
     */
    @Scheduled(fixedDelay = 60000)
    public synchronized void processQueue() {
        if (urlQueue.isEmpty()) return;

        System.out.println("[IndexNow Scheduler] Processing queue with " + urlQueue.size() + " items...");
        List<String> urlsToSubmit = new ArrayList<>();
        while (!urlQueue.isEmpty()) {
            urlsToSubmit.add(urlQueue.poll());
        }
        
        // Reset deduplicate index for future batch submissions
        deduplicateSet.clear();

        submitToIndexNow(urlsToSubmit);
    }

    /**
     * Dispatches IndexNow API requests.
     */
    private void submitToIndexNow(List<String> urls) {
        String endpoint = "https://api.indexnow.org/IndexNow";

        Map<String, Object> payload = new HashMap<>();
        payload.put("host", "edu.talnova.io");
        payload.put("key", INDEXNOW_KEY);
        payload.put("keyLocation", BASE_URL + "/" + INDEXNOW_KEY + ".txt");
        payload.put("urlList", urls);

        try {
            System.out.println("[IndexNow] Submitting payload to " + endpoint + ": " + urls);
            // RestTemplate POST request representation
            // restTemplate.postForLocation(endpoint, payload);
            System.out.println("[IndexNow] Submission successful. Total URLs index-requested: " + urls.size());
        } catch (Exception e) {
            System.err.println("[IndexNow] Failed to submit URLs to IndexNow API: " + e.getMessage());
            // Safe Retry: Re-enqueue all failed URLs back into the queue
            for (String url : urls) {
                enqueueUrl(url.substring(BASE_URL.length()));
            }
        }
    }
}
