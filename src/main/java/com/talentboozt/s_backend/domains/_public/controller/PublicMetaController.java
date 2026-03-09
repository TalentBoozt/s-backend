package com.talentboozt.s_backend.domains._public.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/public")
public class PublicMetaController {

    @GetMapping("/faqs")
    public List<FaqItem> getFaqs() {
        return List.of(
                new FaqItem("Is Talnova free to use?",
                        "Yes! You can create and download a basic resume for free. We also offer premium features for advanced optimization and more templates."),
                new FaqItem("Will my resume be ATS-friendly?",
                        "Absolutely. All our templates are designed and tested to be successfully parsed by major Applicant Tracking Systems."),
                new FaqItem("Can I download my resume in Word format?",
                        "Yes, you can export your resume as both PDF and Microsoft Word (.docx) files."),
                new FaqItem("Is my data secure?",
                        "We take your privacy seriously. Your data is encrypted and we never sell your personal information to third parties."),
                new FaqItem("Can I create multiple resumes?",
                        "Yes, you can create and manage multiple versions of your resume for different job applications in your dashboard."));
    }

    @GetMapping("/resume-examples")
    public List<ResumeExample> getExamples() {
        return List.of(
                new ResumeExample("Senior Software Engineer", "TechCo", "Software Engineering", 12),
                new ResumeExample("Marketing Manager", "Brandify", "Marketing & Sales", 8),
                new ResumeExample("Data Scientist", "DataInc", "Software Engineering", 5),
                new ResumeExample("Product Designer", "CreativeFlow", "Design & Creative", 9),
                new ResumeExample("Research Assistant", "University", "Education & Research", 6));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FaqItem {
        private String question;
        private String answer;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResumeExample {
        private String title;
        private String organization;
        private String category;
        private int pages;
    }
}
