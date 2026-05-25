package com.talentboozt.s_backend.domains.edu.seo.faq;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dynamic FAQ Generation System.
 * Automatically crafts route-aware Q&A nodes and corresponding FAQPage schema elements.
 */
@Service
public class ProgrammaticFaqService {

    /**
     * Dynamically generates FAQ items based on context type and page slug.
     */
    public List<Map<String, String>> generateFaqs(String pageType, String slug) {
        List<Map<String, String>> faqs = new ArrayList<>();
        String normalizedSlug = slug != null ? slug.replace("-", " ") : "";

        if ("course".equalsIgnoreCase(pageType)) {
            faqs.add(createFaqItem(
                "What future-ready skills are covered in " + normalizedSlug + "?",
                "This syllabus is structured specifically around professional career readiness and digital application. You will acquire practical, modern competencies in demand by industry."
            ));
            faqs.add(createFaqItem(
                "Is this course beginner friendly and suitable for career transitions?",
                "Yes, it starts from base conceptual fundamentals before escalating to professional execution, preparing you comprehensively for remote work roles."
            ));
            faqs.add(createFaqItem(
                "Can this course prepare me for freelancing or remote jobs?",
                "Absolutely. All course materials are optimized with practical, real-world case studies designed to support freelance portfolios and remote hiring processes."
            ));
        } else if ("profile".equalsIgnoreCase(pageType)) {
            faqs.add(createFaqItem(
                "What is the educational background and credentials of " + normalizedSlug + "?",
                "This educator holds certified credentials and industry-grade expertise, offering specialized career-readiness classes and mentoring on the Talnova platform."
            ));
            faqs.add(createFaqItem(
                "How does " + normalizedSlug + " structure academic or professional tracks?",
                "The curriculum is designed sequentially, combining theoretical knowledge with future-ready skills, remote portfolio exercises, and modern digital tools."
            ));
        } else {
            // General Explore / Category FAQs
            faqs.add(createFaqItem(
                "What digital categories are supported on Talnova Education?",
                "We provide specialized learning tracks in AI & Automation, Software Development, Freelancing, Digital Marketing, and Creative Skills."
            ));
            faqs.add(createFaqItem(
                "Does the platform offer verified certifications?",
                "Yes, upon completing certified course curriculums, learners receive shareable digital badges and certificates recognized by remote businesses."
            ));
        }

        return faqs;
    }

    /**
     * Converts a list of FAQs to a valid Schema.org JSON-LD string.
     */
    public String generateFaqSchemaJson(List<Map<String, String>> faqs) {
        if (faqs == null || faqs.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("{\n")
          .append("  \"@context\": \"https://schema.org\",\n")
          .append("  \"@type\": \"FAQPage\",\n")
          .append("  \"mainEntity\": [\n");

        for (int i = 0; i < faqs.size(); i++) {
            Map<String, String> item = faqs.get(i);
            String question = escapeJsonString(item.get("question"));
            String answer = escapeJsonString(item.get("answer"));

            sb.append("    {\n")
              .append("      \"@type\": \"Question\",\n")
              .append("      \"name\": \"").append(question).append("\",\n")
              .append("      \"acceptedAnswer\": {\n")
              .append("        \"@type\": \"Answer\",\n")
              .append("        \"text\": \"").append(answer).append("\"\n")
              .append("      }\n")
              .append("    }");

            if (i < faqs.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("  ]\n")
          .append("}");

        return sb.toString();
    }

    private Map<String, String> createFaqItem(String question, String answer) {
        Map<String, String> item = new HashMap<>();
        item.put("question", question);
        item.put("answer", answer);
        return item;
    }

    private String escapeJsonString(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"");
    }
}
