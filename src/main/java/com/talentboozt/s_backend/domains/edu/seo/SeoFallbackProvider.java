package com.talentboozt.s_backend.domains.edu.seo;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class SeoFallbackProvider {

    public SeoMetadata getFallback(String slug, Map<String, Object> context) {
        String query = (slug != null ? slug : "").toLowerCase();
        
        // 1. AI & FUTURE SKILLS
        if (query.contains("ai") || query.contains("chatgpt") || query.contains("prompt") || query.contains("generative") || query.contains("machine-learning")) {
            return DynamicSeoFactory.createBaseSeo(
                slug,
                "Future-Ready AI & Generative Engineering Courses | Talnova",
                "Master ChatGPT, prompt engineering, AI automation, and machine learning tools online. Build modern digital skills to thrive in the automation era.",
                "AI courses, ChatGPT learning, prompt engineering, generative AI, AI automation, machine learning"
            );
        }
        
        // 2. FREELANCING & ONLINE INCOME
        if (query.contains("freelance") || query.contains("fiverr") || query.contains("upwork") || query.contains("income") || query.contains("earning")) {
            return DynamicSeoFactory.createBaseSeo(
                slug,
                "Freelancing Masters & Digital Income Academy | Talnova",
                "Learn how to build high-converting portfolios, win high-paying international clients on Upwork and Fiverr, and succeed in the global creator economy.",
                "freelancing, Fiverr, Upwork, digital income, creator economy, online earning, client communication"
            );
        }

        // 3. TECH & DEVELOPMENT
        if (query.contains("dev") || query.contains("react") || query.contains("node") || query.contains("python") || query.contains("web") || query.contains("programming") || query.contains("software")) {
            return DynamicSeoFactory.createBaseSeo(
                slug,
                "Professional Software Engineering & Coding Bootcamps | Talnova",
                "Accelerate your career with industry-guided training in React, Node.js, Python, full stack web development, and cloud computing architectures.",
                "Web development, full stack engineering, React, Node.js, Python, software engineering bootcamps"
            );
        }

        // 4. DIGITAL MARKETING
        if (query.contains("marketing") || query.contains("ads") || query.contains("seo") || query.contains("tiktok") || query.contains("social-media")) {
            return DynamicSeoFactory.createBaseSeo(
                slug,
                "Advanced Digital Marketing & Brand Growth Strategies | Talnova",
                "Learn Facebook/Meta Ads, high-growth TikTok marketing, search engine optimization (SEO), and conversion analytics to scale modern businesses.",
                "digital marketing, SEO, Meta Ads, TikTok marketing, social media growth, content marketing"
            );
        }

        // 5. CREATIVE SKILLS
        if (query.contains("design") || query.contains("edit") || query.contains("creative") || query.contains("canva") || query.contains("ui") || query.contains("ux")) {
            return DynamicSeoFactory.createBaseSeo(
                slug,
                "Creative Design, UI/UX & High-Impact Video Editing | Talnova",
                "Develop in-demand creative skills. Master graphic design in Canva, professional video editing, motion graphics, and UI/UX designer tools.",
                "graphic design, Canva, video editing, motion graphics, UI/UX, content creation"
            );
        }

        // 6. BUSINESS & ENTREPRENEURSHIP
        if (query.contains("business") || query.contains("startup") || query.contains("entrepreneur") || query.contains("finance")) {
            return DynamicSeoFactory.createBaseSeo(
                slug,
                "Modern Entrepreneurship, Startups & Digital Commerce | Talnova",
                "Launch your startup, understand business automation and e-commerce models, and develop crucial financial literacy to scale digital ventures.",
                "startups, entrepreneurship, online business, business automation, e-commerce, financial literacy"
            );
        }

        // 7. CAREER & JOB READINESS
        if (query.contains("career") || query.contains("job") || query.contains("interview") || query.contains("resume") || query.contains("ats")) {
            return DynamicSeoFactory.createBaseSeo(
                slug,
                "Career Readiness & ATS Resume Optimization Masterclass | Talnova",
                "Prepare for top remote jobs with ATS-optimized resume building, LinkedIn branding strategies, and professional interview coaching.",
                "resume building, ATS optimization, LinkedIn optimization, interview prep, remote work career readiness"
            );
        }

        // 8. SCHOOL & ACADEMIC SUPPORT (Preserved, but balanced with future skills context)
        if (query.contains("al") || query.contains("ol") || query.contains("exam") || query.contains("math") || query.contains("physics") || query.contains("ict") || query.contains("tuition")) {
            return DynamicSeoFactory.createBaseSeo(
                slug,
                "Balanced Exam Prep & Career Skills Platform | Talnova",
                "Excel in G.C.E. Advanced Level (A/L) and Ordinary Level (O/L) Mathematics, Physics, and ICT revision classes while preparing for future career paths.",
                "A/L exam prep, O/L mathematics, revision tuition materials, exam revision study guides, ICT courses"
            );
        }

        // GENERAL PLATFORM FALLBACK (Repositioned around future skills & professional learning)
        return DynamicSeoFactory.createBaseSeo(
            slug,
            "Professional Skills, AI Courses & Career Development | Talnova",
            "Empower your career journey with verified online courses in Generative AI, freelancing, modern software development, digital marketing, and academic support.",
            "professional skills, future skills, career development, AI education, online courses"
        );
    }
}
