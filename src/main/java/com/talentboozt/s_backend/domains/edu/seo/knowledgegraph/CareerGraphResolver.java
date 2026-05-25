package com.talentboozt.s_backend.domains.edu.seo.knowledgegraph;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CareerGraphResolver {

    /**
     * Maps courses to specific high-paying career paths and roles.
     */
    public List<String> resolvePreparesForCareers(ECourses course) {
        List<String> careers = new ArrayList<>();
        if (course.getCategories() == null || course.getCategories().length == 0) {
            careers.add("Remote Freelancer");
            return careers;
        }

        String cat = course.getCategories()[0].toUpperCase();
        if (cat.contains("DEVELOPMENT") || cat.contains("SOFTWARE")) {
            careers.addAll(List.of("Full Stack Developer", "Front End Engineer", "Software Architect"));
        } else if (cat.contains("AI") || cat.contains("AUTOMATION")) {
            careers.addAll(List.of("AI Prompt Engineer", "Automation Specialist", "AI Operations Consultant"));
        } else if (cat.contains("MARKETING")) {
            careers.addAll(List.of("Growth Marketer", "SEO Architect", "Social Media Strategist"));
        } else if (cat.contains("CREATIVE") || cat.contains("DESIGN")) {
            careers.addAll(List.of("UI/UX Designer", "Brand Identity Designer", "Product Designer"));
        } else {
            careers.addAll(List.of("Freelancer", "Independent Contractor", "Consultant"));
        }

        return careers;
    }
}
