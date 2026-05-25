package com.talentboozt.s_backend.domains.edu.seo.knowledgegraph;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SkillGraphResolver {

    /**
     * Resolves the list of skills taught by a course.
     */
    public List<String> resolveTeachesSkills(ECourses course) {
        List<String> skills = new ArrayList<>();
        if (course.getSkills() != null) {
            skills.addAll(Arrays.asList(course.getSkills()));
        }
        if (skills.isEmpty()) {
            skills.addAll(List.of("Problem Solving", "Professional Application"));
        }
        return skills;
    }

    /**
     * Resolves software tools related to the subject matter.
     */
    public List<String> resolveRelatedTools(ECourses course) {
        List<String> tools = new ArrayList<>();
        if (course.getTags() != null) {
            for (String tag : course.getTags()) {
                if (isTool(tag)) {
                    tools.add(tag);
                }
            }
        }
        if (tools.isEmpty()) {
            tools.add("Digital Workplace Tools");
        }
        return tools;
    }

    private boolean isTool(String tag) {
        String t = tag.toLowerCase();
        return t.contains("figma") || t.contains("github") || t.contains("react") || 
               t.contains("chatgpt") || t.contains("canva") || t.contains("vs code") || 
               t.contains("excel") || t.contains("python");
    }
}
