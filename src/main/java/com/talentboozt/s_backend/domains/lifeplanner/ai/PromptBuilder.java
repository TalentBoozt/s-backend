package com.talentboozt.s_backend.domains.lifeplanner.ai;

import org.springframework.stereotype.Component;
import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserProfile;
import java.util.List;

@Component
public class PromptBuilder {

    public String buildPlanGenerationPrompt(Goal goal, UserProfile userProfile) {
        return """
                You are an expert AI life coach and study planner.

                ## USER CONTEXT
                - Goal: %s
                - Description: %s
                - Deadline: %s
                - Difficulty: %s
                - Daily Focus Time Available: %s
                - Stress Level: %s
                - Hobbies/Interests: %s

                ## INSTRUCTIONS
                Generate a comprehensive, adaptive study/action plan. Break it down into:
                1. A high-level roadmap with sequential phases
                2. A week-by-week breakdown with focus areas
                3. Daily actionable tasks with estimated durations

                Each daily task must be achievable within the user's available focus time.
                Balance intensity based on stress level (lower stress = can handle more load).
                Incorporate hobby-related breaks for engagement.

                ## REQUIRED JSON RESPONSE FORMAT
                Return valid JSON matching this exact structure:
                ```json
                {
                  "roadmap": [
                    {
                      "title": "Phase 1: Foundation",
                      "description": "Build core understanding",
                      "expectedDuration": "2 weeks"
                    }
                  ],
                  "weeklyPlans": [
                    {
                      "weekNumber": 1,
                      "focusArea": "Introduction and basics",
                      "objectives": ["Learn X", "Practice Y"]
                    }
                  ],
                  "dailyTasks": [
                    {
                      "title": "Read Chapter 1",
                      "estimatedTime": "45 min",
                      "category": "Reading",
                      "priority": "HIGH"
                    }
                  ]
                }
                ```

                Generate at least 3 roadmap phases, 4 weekly plans, and 14 daily tasks.
                """.formatted(
                goal.getTitle(),
                goal.getDescription() != null ? goal.getDescription() : "N/A",
                goal.getDeadline() != null ? goal.getDeadline().toString() : "Flexible",
                goal.getDifficulty() != null ? goal.getDifficulty() : "Medium",
                userProfile.getFocusTime() != null ? userProfile.getFocusTime() : "2 hours",
                userProfile.getStressLevel() != null ? userProfile.getStressLevel() : "Medium",
                userProfile.getHobbies() != null ? String.join(", ", userProfile.getHobbies()) : "N/A"
        );
    }

    public String buildScheduleOptimizationPrompt(List<String> missedTasks) {
        return """
                You are a schedule optimization assistant.

                ## MISSED TASKS
                The user has not completed the following tasks from their plan:
                %s

                ## INSTRUCTIONS
                Redistribute these tasks over the next 7 days, ensuring:
                1. No single day is overloaded (max 3 hours of catch-up per day)
                2. Higher priority tasks come first
                3. Provide a rationale for the redistribution strategy

                ## REQUIRED JSON RESPONSE FORMAT
                ```json
                {
                  "rescheduledTasks": [
                    {
                      "title": "Task name",
                      "estimatedTime": "30 min",
                      "assignedDay": "2026-03-15",
                      "category": "Reading"
                    }
                  ],
                  "rationale": "Brief explanation of the rescheduling strategy"
                }
                ```
                """.formatted(String.join("\n- ", missedTasks));
    }

    public String buildJournalPrompt(UserProfile userProfile) {
        return """
                You are an empathetic journaling coach.

                ## USER CONTEXT
                - Stress Level: %s
                - Hobbies: %s
                - Focus Time Preference: %s

                Generate a single thoughtful journaling prompt (1-2 sentences) that:
                1. Encourages self-reflection on today's progress
                2. Connects to their interests when possible
                3. Is appropriate for their current stress level

                Return the prompt as a plain text string, not JSON.
                """.formatted(
                userProfile.getStressLevel() != null ? userProfile.getStressLevel() : "Medium",
                userProfile.getHobbies() != null ? String.join(", ", userProfile.getHobbies()) : "general interests",
                userProfile.getFocusTime() != null ? userProfile.getFocusTime() : "moderate"
        );
    }
}
