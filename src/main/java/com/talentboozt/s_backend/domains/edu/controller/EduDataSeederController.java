package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.enums.ECourseLevel;
import com.talentboozt.s_backend.domains.edu.enums.ECourseStatus;
import com.talentboozt.s_backend.domains.edu.enums.ECourseType;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/edu/seeder")
@ConditionalOnProperty(name = "app.edu.seeder.enabled", havingValue = "true")
public class EduDataSeederController {

    private final ECoursesRepository coursesRepository;

    public EduDataSeederController(ECoursesRepository coursesRepository) {
        this.coursesRepository = coursesRepository;
    }

    @PostMapping("/run")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<String> seedDemoData() {
        if (coursesRepository.count() == 0) {
            ECourses demoCourse = ECourses.builder()
                    .creatorId("DEMO_CREATOR_" + UUID.randomUUID().toString().substring(0, 8))
                    .title("Master Java Spring Boot")
                    .description("The complete boot camp covering all core aspects of building scalable architecture.")
                    .shortDescription("Build Enterprise APIs")
                    .thumbnail("default_demo_thumbnail.jpg")
                    .type(ECourseType.COURSE)
                    .price(49.99)
                    .currency("USD")
                    .published(true)
                    .isPrivate(false)
                    .level(ECourseLevel.INTERMEDIATE)
                    .language("en")
                    .tags(new String[] { "java", "spring", "backend", "api" })
                    .categories(new String[] { "Development", "Software Engineering" })
                    .rating(4.8)
                    .totalEnrollments(1500)
                    .totalReviews(200)
                    .totalHours(25)
                    .totalLessons(80)
                    .isFeatured(true)
                    .isTrending(true)
                    .searchRank(100)
                    .status(ECourseStatus.PUBLISHED)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .publishedAt(Instant.now())
                    .slug("master-java-spring-boot-" + UUID.randomUUID().toString().substring(0, 6))
                    .build();
            coursesRepository.save(demoCourse);
            return ResponseEntity.ok("Successfully seeded 1 Demo Course for UI staging.");
        }
        return ResponseEntity.ok("Database already contains records. Seeder aborted to prevent duplicates.");
    }
}
