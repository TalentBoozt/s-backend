package com.talentboozt.s_backend.domains.edu.programmatic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

/**
 * Programmatic SEO Page RestController.
 * Exposes paths for dynamic client-side template delivery and indexing compilation triggers.
 */
@RestController
@RequestMapping("/api/v1/edu/programmatic")
@CrossOrigin(origins = "*")
public class ProgrammaticPageController {

    @Autowired
    private ProgrammaticPageService service;

    /**
     * Resolves programmatic page variables matching complex path structures.
     * Supports wildcards for routes like /tuition/physics/colombo.
     */
    @GetMapping("/{*slug}")
    public ResponseEntity<ProgrammaticPageDocument> getProgrammaticPage(@PathVariable String slug) {
        String cleanSlug = (slug != null && slug.startsWith("/")) ? slug.substring(1) : slug;
        Optional<ProgrammaticPageDocument> page = service.getPageBySlug(cleanSlug);
        
        return page.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Post hook to execute scale database generation manually.
     */
    @PostMapping("/generate")
    public ResponseEntity<String> triggerGeneration() {
        service.generateAllProgrammaticPages();
        return ResponseEntity.ok("Programmatic scale generation executed successfully.");
    }
}
