package com.talentboozt.s_backend.domains.leads.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class LAIService {

    // Simple cache to avoid redundant AI calls for the same prompt/context
    private final Cache<String, String> replyCache = Caffeine.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(1000)
            .build();

    @RateLimiter(name = "aiService")
    public String generateReply(String content, String tone) {
        String cacheKey = tone + ":" + content.hashCode();
        return replyCache.get(cacheKey, key -> {
            // In production, this would call LOpenAIClient or LAnthropicClient
            return performActualAiGeneration(content, tone);
        });
    }

    private String performActualAiGeneration(String content, String tone) {
        // This is where real API calls (OpenAI/Anthropic) would happen
        // For now, still mocking but prepared for extension
        if ("helpful".equalsIgnoreCase(tone)) {
            return "Hey! I saw your post. I think " + extractTheme(content)
                    + " is really interesting. Have you considered looking into LeadOS? It might help!";
        } else if ("pitch".equalsIgnoreCase(tone)) {
            return "Hi there, I'm from LeadOS. We specialize in " + extractTheme(content)
                    + ". I'd love to show you how we can automate your pipeline!";
        } else {
            return "Great point about " + extractTheme(content) + ". I'd love to chat more about it!";
        }
    }

    private String extractTheme(String content) {
        if (content == null || content.isEmpty())
            return "this";
        String[] words = content.split("\\s+");
        return words.length > 3 ? words[0] + " " + words[1] + " " + words[2] : "your post";
    }
}
