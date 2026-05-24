package com.talentboozt.s_backend.domains.edu.career;

import org.springframework.stereotype.Service;

/**
 * AI Career Advisor and Coach Service.
 * Formulates interactive portfolio strategies, interview advice, and transition schedules
 * to support multi-domain students.
 */
@Service
public class CareerCoachService {

    /**
     * Synthesizes strategic advice matching career goals and questions.
     */
    public String generateCoachAdvice(String targetCareer, String userQuestion) {
        if (targetCareer == null) return "Focus on dynamic learning pathways and earn verified certifications.";
        String normalized = targetCareer.toLowerCase().trim();
        
        if (normalized.contains("frontend") || normalized.contains("developer")) {
            return "To stand out as a Junior Frontend Developer, build high-quality responsive React projects, host them on Netlify/Vercel, and optimize your GitHub with clean commit descriptions.";
        }
        if (normalized.contains("freelance") || normalized.contains("designer")) {
            return "Focus on building a specialized visual portfolio on Behance or Dribbble. Set up structured gigs on Fiverr or Upwork with highly descriptive, SEO-optimized service tags.";
        }
        
        return "Continuous micro-learning is the key. Complete your daily study streaks, gain XP, and earn verified skill badges to optimize your job applications.";
    }
}
