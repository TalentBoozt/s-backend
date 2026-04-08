package com.talentboozt.s_backend.domains.leads.dto;

import lombok.Data;

import java.util.List;

@Data
public class LRedditResponseDTO {
    private String kind;
    private RedditData data;

    @Data
    public static class RedditData {
        private List<RedditChild> children;
    }

    @Data
    public static class RedditChild {
        private String kind;
        private RedditPost data;
    }

    @Data
    public static class RedditPost {
        private String name; // unique id (e.g., t3_...)
        private String title;
        private String selftext;
        private String author;
        private String url;
        private String permalink;
        private Long created_utc;
        private String subreddit;
    }
}
