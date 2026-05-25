package com.talentboozt.s_backend.domains.edu.seo.analytics;

import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class BotClassificationEngine {

    /**
     * Identifies if a given User-Agent belongs to a known AI/LLM bot.
     */
    public String classifyUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return null;
        }

        String ua = userAgent.toLowerCase(Locale.ROOT);

        if (ua.contains("gptbot")) {
            return "GPTBot (OpenAI)";
        } else if (ua.contains("chatgpt-user")) {
            return "ChatGPT-User (OpenAI)";
        } else if (ua.contains("claudebot")) {
            return "ClaudeBot (Anthropic)";
        } else if (ua.contains("perplexitybot")) {
            return "PerplexityBot (Perplexity)";
        } else if (ua.contains("google-extended")) {
            return "Google-Extended (Gemini)";
        } else if (ua.contains("oai-searchbot") || ua.contains("openai")) {
            return "OAI-SearchBot (OpenAI)";
        } else if (ua.contains("ccbot")) {
            return "CCBot (Common Crawl)";
        } else if (ua.contains("bytespider")) {
            return "Bytespider (ByteDance)";
        } else if (ua.contains("amazonbot")) {
            return "Amazonbot (Amazon)";
        } else if (ua.contains("meta-externalagent")) {
            return "Meta-ExternalAgent (Meta)";
        } else if (ua.contains("googlebot")) {
            return "Googlebot";
        } else if (ua.contains("bingbot")) {
            return "Bingbot";
        } else if (ua.contains("yandex") || ua.contains("baidu")) {
            return "Other Search Crawler";
        }

        return null; // Not classified as an AI or primary crawler
    }
}
