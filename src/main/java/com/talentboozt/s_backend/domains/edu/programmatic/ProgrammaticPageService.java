package com.talentboozt.s_backend.domains.edu.programmatic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.edu.model.EProgrammaticPage;
import java.util.List;
import java.util.Optional;

/**
 * Programmatic Landing Page Service.
 * Manages database initialization of dynamic targets, checks for duplicates,
 * and handles run-time queries for dynamic template loaders.
 */
@Service
public class ProgrammaticPageService {

    @Autowired
    private ProgrammaticPageRepository repository;

    @Autowired
    private ProgrammaticKeywordGenerator keywordGenerator;

    @Autowired
    private ProgrammaticTemplateEngine templateEngine;

    /**
     * Loops through dynamic keyword patterns and compiles/saves missing landing pages safely.
     */
    public void generateAllProgrammaticPages() {
        List<String> targetSlugs = keywordGenerator.generateTargetSlugs();
        System.out.println("Starting programmatic scale generation for " + targetSlugs.size() + " pages...");
        
        int count = 0;
        for (String slug : targetSlugs) {
            Optional<EProgrammaticPage> existing = repository.findBySlug(slug);
            if (existing.isEmpty()) {
                EProgrammaticPage page = templateEngine.buildPage(slug);
                repository.save(page);
                count++;
            }
        }
        System.out.println("Programmatic scale generation completed successfully. Backfilled " + count + " new pages.");
    }

    /**
     * Resolves a programmatic landing page by its slug route.
     */
    public Optional<EProgrammaticPage> getPageBySlug(String slug) {
        return repository.findBySlug(slug);
    }
}
