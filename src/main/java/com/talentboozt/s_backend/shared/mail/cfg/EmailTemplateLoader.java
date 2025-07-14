package com.talentboozt.s_backend.shared.mail.cfg;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class EmailTemplateLoader {

    public String loadTemplate(String templateName, Map<String, String> variables) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + templateName);
        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            content = content.replace("${" + entry.getKey() + "}", entry.getValue());
        }

        return content;
    }
}
