package com.talentboozt.s_backend.shared.ai.port;

import java.util.List;
import java.util.Map;

/**
 * Port interface for AI/LLM services.
 * Products use this to access AI capabilities without depending
 * on specific LLM providers or the AI tool infrastructure directly.
 */
public interface AIServicePort {

    /**
     * Send a chat completion request.
     *
     * @param systemPrompt  the system prompt
     * @param userMessage   the user message
     * @param model         optional model override (null for default)
     * @return the AI-generated response
     */
    String chat(String systemPrompt, String userMessage, String model);

    /**
     * Send a chat completion with conversation history.
     */
    String chatWithHistory(String systemPrompt, List<Map<String, String>> messages, String model);
}
