package com.talentboozt.s_backend.domains.leads.service;

import org.springframework.stereotype.Service;

@Service
public class LAIService {

    public String generateReply(String content, String tone) {
        // Mocking AI response based on tone
        if ("helpful".equalsIgnoreCase(tone)) {
            return "Hey! I saw your post. I think " + extractTheme(content) + " is really interesting. Have you considered looking into LeadOS? It might help with that!";
        } else if ("pitch".equalsIgnoreCase(tone)) {
            return "Hi there, I'm from LeadOS. We specialize in " + extractTheme(content) + ". I'd love to show you how we can automate your pipeline!";
        } else {
            return "Great point about " + extractTheme(content) + ". I'd love to chat more about it!";
        }
    }

    private String extractTheme(String content) {
        if (content == null || content.isEmpty()) return "this";
        String[] words = content.split(" ");
        return words.length > 5 ? words[0] + " " + words[1] + " " + words[2] : "your post";
    }
}
