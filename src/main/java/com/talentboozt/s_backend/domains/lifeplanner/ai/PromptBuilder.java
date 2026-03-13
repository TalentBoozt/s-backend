package com.talentboozt.s_backend.domains.lifeplanner.ai;

import org.springframework.stereotype.Component;
import com.talentboozt.s_backend.domains.lifeplanner.goal.model.Goal;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserProfile;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserPreferences;
import java.util.List;

@Component
public class PromptBuilder {

    public String buildPlanGenerationPrompt(Goal goal, UserProfile userProfile, UserPreferences prefs) {
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
                
                ## SCHEDULING PREFERENCES
                - Work Hours: %s to %s
                - Productivity Cycle: %s
                - Preferred Break Frequency: every %d minutes
                - Preferred Session Length: %d minutes
                - Planning Aesthetic: %s

                ## INSTRUCTIONS
                Generate a comprehensive, adaptive study/action plan. Break it down into:
                1. A high-level roadmap with sequential phases
                2. A week-by-week breakdown with focus areas
                3. Daily actionable tasks with estimated durations

                Each daily task must be achievable within the user's available focus time and should ideally fit within their specified work hours and productivity cycle.
                
                %s
                
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
                userProfile.getHobbies() != null ? String.join(", ", userProfile.getHobbies()) : "N/A",
                prefs.getWorkHoursStart() != null ? prefs.getWorkHoursStart() : "09:00",
                prefs.getWorkHoursEnd() != null ? prefs.getWorkHoursEnd() : "17:00",
                prefs.getProductivityCycle() != null ? prefs.getProductivityCycle() : "flexible",
                prefs.getBreakFrequency() > 0 ? prefs.getBreakFrequency() : 25,
                prefs.getStudySessionLength() > 0 ? prefs.getStudySessionLength() : 50,
                prefs.getPlannerLayoutStyle() != null ? prefs.getPlannerLayoutStyle() : "clean",
                goal.getType() == com.talentboozt.s_backend.domains.lifeplanner.goal.model.GoalType.HABIT_BUILDING 
                    ? "As this is a HABIT BUILDING goal, focus on repetitive consistency. The daily tasks should be small, sustainable, and build towards a streak."
                    : "As this is a results-oriented goal, focus on sequential progress and milestones."
        );
    }

    public String buildScheduleOptimizationPrompt(List<String> missedTasks, UserPreferences prefs) {
        return """
                You are a schedule optimization assistant.

                ## MISSED TASKS
                The user has not completed the following tasks from their plan:
                - %s

                ## USER PREFERENCES
                - Work Window: %s to %s
                - Peak Productivity: %s
                - Break Frequency: every %d minutes

                ## INSTRUCTIONS
                Redistribute these tasks over the next 7 days, ensuring:
                1. No single day is overloaded (max 3 hours of catch-up per day)
                2. Higher priority tasks come first
                3. Respect the user's work window and productivity peaks
                4. Provide a rationale for the redistribution strategy

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
                """.formatted(
                        String.join("\n- ", missedTasks),
                        prefs.getWorkHoursStart() != null ? prefs.getWorkHoursStart() : "09:00",
                        prefs.getWorkHoursEnd() != null ? prefs.getWorkHoursEnd() : "17:00",
                        prefs.getProductivityCycle() != null ? prefs.getProductivityCycle() : "flexible",
                        prefs.getBreakFrequency() > 0 ? prefs.getBreakFrequency() : 25
                );
    }

    public String buildJournalPrompt(UserProfile userProfile, UserPreferences prefs) {
        return """
                You are an empathetic journaling coach.

                ## USER CONTEXT
                - Stress Level: %s
                - Hobbies: %s
                - Focus Time Preference: %s
                - Journaling Style: %s

                Generate a single thoughtful journaling prompt (1-2 sentences) that:
                1. Encourages self-reflection on today's progress
                2. Connects to their interests when possible
                3. Matches their preferred journaling style (%s)
                4. Is appropriate for their current stress level

                Return the prompt as a plain text string, not JSON.
                """.formatted(
                userProfile.getStressLevel() != null ? userProfile.getStressLevel() : "Medium",
                userProfile.getHobbies() != null ? String.join(", ", userProfile.getHobbies()) : "general interests",
                userProfile.getFocusTime() != null ? userProfile.getFocusTime() : "moderate",
                prefs.getJournalingStyle() != null ? prefs.getJournalingStyle() : "freeform",
                prefs.getJournalingStyle() != null ? prefs.getJournalingStyle() : "freeform"
        );
    }
}
