package com.talentboozt.s_backend.domains.leads.service;

import com.talentboozt.s_backend.domains.leads.dto.LRedditResponseDTO;
import com.talentboozt.s_backend.domains.leads.events.LNewSignalEvent;
import com.talentboozt.s_backend.domains.leads.model.LLeadSource;
import com.talentboozt.s_backend.domains.leads.model.LRawSignal;
import com.talentboozt.s_backend.domains.leads.repository.LLeadSourceRepository;
import com.talentboozt.s_backend.domains.leads.repository.LRawSignalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LRedditCollectorService {

    private static final Logger log = LoggerFactory.getLogger(LRedditCollectorService.class);

    private final LLeadSourceRepository leadSourceRepository;
    private final LRawSignalRepository rawSignalRepository;
    private final LRedditClient redditClient;
    private final ApplicationEventPublisher eventPublisher;

    public LRedditCollectorService(
            LLeadSourceRepository leadSourceRepository,
            LRawSignalRepository rawSignalRepository,
            LRedditClient redditClient,
            ApplicationEventPublisher eventPublisher) {
        this.leadSourceRepository = leadSourceRepository;
        this.rawSignalRepository = rawSignalRepository;
        this.redditClient = redditClient;
        this.eventPublisher = eventPublisher;
    }

    // Run every 10 minutes
    @Scheduled(fixedDelay = 600000)
    public void fetchRedditLeads() {
        log.debug("Starting Reddit collector cycle...");

        List<LLeadSource> activeRedditSources = leadSourceRepository.findByPlatformAndActive("REDDIT", true);

        int totalFetched = 0;
        int newSignals = 0;

        for (LLeadSource source : activeRedditSources) {
            Map<String, Object> config = source.getConfig();
            if (config == null || !config.containsKey("subreddits") || !config.containsKey("keywords")) {
                log.warn("Reddit source {} is missing subreddits or keywords configuration.", source.getId());
                continue;
            }

            @SuppressWarnings("unchecked")
            List<String> subreddits = (List<String>) config.get("subreddits");
            @SuppressWarnings("unchecked")
            List<String> keywords = (List<String>) config.get("keywords");

            for (String subreddit : subreddits) {
                for (String keyword : keywords) {
                    try {
                        LRedditResponseDTO response = redditClient.searchSubreddit(subreddit, keyword, 25);
                        if (response != null && response.getData() != null
                                && response.getData().getChildren() != null) {
                            for (LRedditResponseDTO.RedditChild child : response.getData().getChildren()) {
                                LRedditResponseDTO.RedditPost post = child.getData();
                                totalFetched++;

                                // Deduplicate using the Reddit name ID (e.g., t3_xxx)
                                if (!rawSignalRepository.existsByPlatformId(post.getName())) {
                                    LRawSignal signal = createSignal(source, post);
                                    rawSignalRepository.save(signal);
                                    newSignals++;
                                    eventPublisher.publishEvent(new LNewSignalEvent(this, signal));
                                }
                            }
                        }
                        // Sleep to avoid strict rate limiting
                        Thread.sleep(3000);

                    } catch (Exception e) {
                        log.error("Error processing subreddit {} with keyword {}", subreddit, keyword, e);
                    }
                }
            }
        }

        // log.info("Completed Reddit collector cycle. Total fetched: {}, New signals
        // saved: {}", totalFetched,
        // newSignals);

    }

    private LRawSignal createSignal(LLeadSource source, LRedditResponseDTO.RedditPost post) {
        LRawSignal signal = new LRawSignal();
        signal.setSourceId(source.getId());
        signal.setWorkspaceId(source.getWorkspaceId());
        signal.setPlatformId(post.getName());

        // Combine title and selftext
        String content = post.getTitle() + "\n\n" + (post.getSelftext() != null ? post.getSelftext() : "");
        signal.setContent(content);
        signal.setAuthor(post.getAuthor());
        signal.setUrl("https://www.reddit.com" + post.getPermalink());
        signal.setStatus("NEW");
        signal.setCapturedAt(Instant.now());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("platform", "REDDIT");
        metadata.put("subreddit", post.getSubreddit());
        metadata.put("created_utc", post.getCreated_utc());
        signal.setMetadata(metadata);

        return signal;
    }
}
