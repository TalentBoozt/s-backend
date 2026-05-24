package com.talentboozt.s_backend.domains.edu.seo.og;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.TimeUnit;

/**
 * Dynamic Open Graph (OG) Image Controller.
 * Exposes a rest endpoint to serve dynamically drawn PNG share graphics
 * for educational courses with high-performance HTTP edge caching parameters.
 */
@RestController
public class CourseOgImageController {

    @Autowired
    private OgImageService ogImageService;

    /**
     * Serves dynamic course OG card graphics dynamically based on the target slug and attributes.
     */
    @GetMapping(value = "/api/v1/edu/seo/og/course/{slug}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getCourseOgImage(
            @PathVariable String slug,
            @RequestParam(value = "title", defaultValue = "Combined Mathematics A/L Theory") String title,
            @RequestParam(value = "instructor", defaultValue = "Dr. Nishantha Kumara") String instructor,
            @RequestParam(value = "medium", defaultValue = "Sinhala") String medium) {

        byte[] imageBytes = ogImageService.generateCourseOgImage(title, instructor, medium);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
                .body(imageBytes);
    }
}
