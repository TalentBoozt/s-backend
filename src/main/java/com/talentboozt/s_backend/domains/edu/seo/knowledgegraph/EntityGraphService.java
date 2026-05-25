package com.talentboozt.s_backend.domains.edu.seo.knowledgegraph;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Educational Knowledge Graph System.
 * Synthesizes deep semantic relationships and returns high-authority Schema.org structured data.
 */
@Service
public class EntityGraphService {

    @Autowired
    private CourseRelationshipResolver courseResolver;

    @Autowired
    private SkillGraphResolver skillResolver;

    @Autowired
    private CareerGraphResolver careerResolver;

    /**
     * Synthesizes a deep nested Knowledge Graph schema for an educational course.
     */
    public String compileCourseKnowledgeGraph(ECourses course, List<ECourses> allCourses) {
        List<String> teaches = skillResolver.resolveTeachesSkills(course);
        List<String> tools = skillResolver.resolveRelatedTools(course);
        List<String> careers = careerResolver.resolvePreparesForCareers(course);
        List<String> related = courseResolver.resolveRelatedCourses(course, allCourses);

        StringBuilder sb = new StringBuilder();
        sb.append("{\n")
          .append("  \"@context\": \"https://schema.org\",\n")
          .append("  \"@graph\": [\n")
          .append("    {\n")
          .append("      \"@type\": \"Course\",\n")
          .append("      \"@id\": \"https://edu.talnova.io/course/").append(course.getSeoSlug()).append("\",\n")
          .append("      \"name\": \"").append(course.getTitle()).append("\",\n")
          .append("      \"description\": \"").append(course.getShortDescription() != null ? course.getShortDescription() : "").append("\",\n")
          .append("      \"provider\": {\n")
          .append("        \"@type\": \"EducationalOrganization\",\n")
          .append("        \"name\": \"Talnova\",\n")
          .append("        \"url\": \"https://edu.talnova.io\"\n")
          .append("      },\n")
          .append("      \"teaches\": [\n");

        for (int i = 0; i < teaches.size(); i++) {
            sb.append("        \"").append(teaches.get(i)).append("\"");
            if (i < teaches.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("      ],\n")
          .append("      \"assessedCompetency\": [\n");

        for (int i = 0; i < tools.size(); i++) {
            sb.append("        \"Uses Tool: ").append(tools.get(i)).append("\"");
            if (i < tools.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("      ],\n")
          .append("      \"occupationalCategory\": [\n");

        for (int i = 0; i < careers.size(); i++) {
            sb.append("        \"").append(careers.get(i)).append("\"");
            if (i < careers.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("      ]\n")
          .append("    }");

        if (!related.isEmpty()) {
            sb.append(",\n")
              .append("    {\n")
              .append("      \"@type\": \"ItemList\",\n")
              .append("      \"name\": \"Related Syllabuses\",\n")
              .append("      \"itemListElement\": [\n");

            for (int i = 0; i < related.size(); i++) {
                sb.append("        {\n")
                  .append("          \"@type\": \"ListItem\",\n")
                  .append("          \"position\": ").append(i + 1).append(",\n")
                  .append("          \"url\": \"").append(related.get(i)).append("\"\n")
                  .append("        }");
                if (i < related.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("      ]\n")
              .append("    }");
        }

        sb.append("\n  ]\n")
          .append("}");

        return sb.toString();
    }
}
