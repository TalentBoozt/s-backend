package com.talentboozt.s_backend.domains.edu.seo.migration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * MongoDB SEO Migration Startup Execution Hook.
 * Implements CommandLineRunner to execute the Schema Evolution backfiller immediately on boot.
 */
@Component
public class SeoDocumentMigrationRunner implements CommandLineRunner {

    private final SeoMigrationService migrationService;

    public SeoDocumentMigrationRunner(SeoMigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @Override
    public void run(String... args) throws Exception {
        migrationService.runSchemaMigration();
    }
}
