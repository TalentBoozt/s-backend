package com.talentboozt.s_backend.shared.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiClient {

    @Value("${openai.api.key}")
    private String apiKey;

    private final ObjectMapper mapper;

    public <T> T callStructuredApi(String prompt, Class<T> responseType) {
        WebClient client = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "max_tokens", 300
        );

        String raw = client.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .map(res -> ((Map<String, String>) ((Map)((List)res.get("choices")).get(0)).get("message")).get("content"))
                .block();

        // 🔥 Remove Markdown formatting like ```json or ```
        String cleaned = raw
                .replaceAll("^```(json)?", "")
                .replaceAll("```$", "")
                .trim();

        try {
            return mapper.readValue(cleaned, responseType);
        } catch (Exception e) {
            throw new RuntimeException("AI returned invalid JSON: " + raw, e);
        }
    }
}
