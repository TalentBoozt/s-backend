package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.EProfiles;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EProfilesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class EduProfileService {

    private static final long MAX_AVATAR_BYTES = 5 * 1024 * 1024;

    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.EProfilesRepository profileRepository;
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository userRepository;
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository courseRepository;
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository enrollmentRepository;
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.ECertificatesRepository certificateRepository;
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.EReviewsRepository reviewRepository;
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.EWorkspacesRepository workspaceRepository;
    private final R2StorageService storageService;

    public EduProfileService(
            EProfilesRepository profileRepository, 
            EUserRepository userRepository,
            com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository courseRepository,
            com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository enrollmentRepository,
            com.talentboozt.s_backend.domains.edu.repository.mongodb.ECertificatesRepository certificateRepository,
            com.talentboozt.s_backend.domains.edu.repository.mongodb.EReviewsRepository reviewRepository,
            com.talentboozt.s_backend.domains.edu.repository.mongodb.EWorkspacesRepository workspaceRepository,
            R2StorageService storageService) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.certificateRepository = certificateRepository;
        this.reviewRepository = reviewRepository;
        this.workspaceRepository = workspaceRepository;
        this.storageService = storageService;
    }

    public EProfiles getProfileByUserId(String userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EduResourceNotFoundException("Profile not found for userId: " + userId));
    }

    @org.springframework.cache.annotation.CacheEvict(cacheNames = "publicProfiles", key = "#userId")
    public EProfiles updateProfile(String userId, EProfiles updatedProfile) {
        EProfiles existing = getProfileByUserId(userId);

        existing.setFirstName(updatedProfile.getFirstName());
        existing.setLastName(updatedProfile.getLastName());
        existing.setPublicEmail(updatedProfile.getPublicEmail());
        existing.setPublicPhone(updatedProfile.getPublicPhone());
        existing.setAvatarUrl(updatedProfile.getAvatarUrl());
        existing.setBio(updatedProfile.getBio());
        existing.setSocialLinks(updatedProfile.getSocialLinks());
        existing.setIndustry(updatedProfile.getIndustry());
        existing.setCompany(updatedProfile.getCompany());
        existing.setJobTitle(updatedProfile.getJobTitle());
        existing.setExperience(updatedProfile.getExperience());
        existing.setEducation(updatedProfile.getEducation());
        existing.setSkills(updatedProfile.getSkills());
        existing.setLanguages(updatedProfile.getLanguages());
        existing.setInterests(updatedProfile.getInterests());
        existing.setPrivacySettings(updatedProfile.getPrivacySettings());
        existing.setNotificationSettings(updatedProfile.getNotificationSettings());
        existing.setUpdatedAt(Instant.now());

        return profileRepository.save(existing);
    }

    /**
     * Saves an image under {@code uploads/edu/profiles/{userId}/} and updates {@link EProfiles#avatarUrl}
     * to a public path served by {@link com.talentboozt.s_backend.config.StaticResourceConfig}.
     */
    public Map<String, String> uploadAvatar(String userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EduBadRequestException("File is required");
        }
        if (file.getSize() > MAX_AVATAR_BYTES) {
            throw new EduBadRequestException("Avatar must be at most 5MB");
        }
        String ct = file.getContentType();
        if (ct == null || !ct.startsWith("image/")) {
            throw new EduBadRequestException("Only image uploads are allowed");
        }
        try {
            String url = storageService.uploadFile(file);
            EProfiles existing = getProfileByUserId(userId);
            existing.setAvatarUrl(url);
            existing.setUpdatedAt(Instant.now());
            profileRepository.save(existing);
            return Map.of("avatarUrl", url);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar to R2", e);
        }
    }

    public void deleteProfileAndUser(String userId) {
        profileRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    @org.springframework.cache.annotation.Cacheable(cacheNames = "publicProfiles", key = "#userId")
    public com.talentboozt.s_backend.domains.edu.dto.profile.PublicProfileDTO getPublicProfile(String userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EduResourceNotFoundException("User not found: " + userId));
        var profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new EduResourceNotFoundException("Profile not found: " + userId));

        var dto = new com.talentboozt.s_backend.domains.edu.dto.profile.PublicProfileDTO();
        dto.setId(userId);
        dto.setName(profile.getFirstName() + " " + profile.getLastName());
        dto.setTitle(profile.getJobTitle());
        dto.setAvatar(profile.getAvatarUrl());
        dto.setBio(profile.getBio());
        dto.setSkills(profile.getSkills());
        dto.setExperiences(java.util.Arrays.stream(profile.getExperience() != null ? profile.getExperience() : new com.talentboozt.s_backend.domains.edu.dto.EExperienceDTO[0])
                .map(e -> e.getJobTitle() + " at " + e.getCompany())
                .toArray(String[]::new));
        
        if (profile.getSocialLinks() != null) {
            Map<String, String> links = new java.util.HashMap<>();
            if (profile.getSocialLinks().getTwitter() != null) links.put("twitter", profile.getSocialLinks().getTwitter());
            if (profile.getSocialLinks().getLinkedin() != null) links.put("linkedin", profile.getSocialLinks().getLinkedin());
            if (profile.getSocialLinks().getGithub() != null) links.put("github", profile.getSocialLinks().getGithub());
            if (profile.getSocialLinks().getYoutube() != null) links.put("youtube", profile.getSocialLinks().getYoutube());
            dto.setSocialLinks(links);
        }

        // Roles
        dto.setRoles(java.util.Arrays.stream(user.getRoles()).map(Enum::name).toArray(String[]::new));
        dto.setPlanShortCode(user.getPlan().name());
        dto.setProMember(user.getPlan() == com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan.PRO || user.getPlan() == com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan.PREMIUM);

        // Stats & Content
        Map<String, Object> stats = new java.util.HashMap<>();
        
        boolean isCreator = java.util.Arrays.stream(user.getRoles()).anyMatch(r -> r.name().startsWith("SELLER") || r == com.talentboozt.s_backend.domains.edu.enums.ERoles.ENTERPRISE_INSTRUCTOR);
        if (isCreator) {
            var courses = courseRepository.findByCreatorId(userId);
            stats.put("coursesPublished", courses.size());
            stats.put("totalEnrollments", courses.stream().mapToInt(c -> c.getTotalEnrollments() != null ? c.getTotalEnrollments() : 0).sum());
            
            dto.setHighlightedContent(courses.stream()
                    .filter(c -> Boolean.TRUE.equals(c.getPublished()))
                    .limit(4)
                    .map(c -> com.talentboozt.s_backend.domains.edu.dto.profile.PortfolioCourseDTO.builder()
                            .id(c.getId()).title(c.getTitle()).thumbnail(c.getThumbnail()).category(c.getCategories() != null && c.getCategories().length > 0 ? c.getCategories()[0] : "Education")
                            .rating(c.getRating()).totalStudents(c.getTotalEnrollments()).price(c.getPrice()).build())
                    .toList());
        }

        var certificates = certificateRepository.findByUserId(userId);
        stats.put("certificatesEarned", certificates.size());
        dto.setAchievements(certificates.stream()
                .limit(6)
                .map(c -> com.talentboozt.s_backend.domains.edu.dto.profile.PortfolioAchievementDTO.builder()
                        .id(c.getId()).title(c.getCourseName()).type("CERTIFICATE").issuerName("Talnova Academy").issuedAt(c.getIssuedAt()).verificationUrl("/verify/" + c.getCertificateId())
                        .build())
                .toList());

        dto.setStats(stats);
        return dto;
    }

    @org.springframework.cache.annotation.Cacheable(cacheNames = "enterprisePortfolios", key = "#workspaceId")
    public com.talentboozt.s_backend.domains.edu.dto.profile.EnterprisePortfolioDTO getEnterprisePortfolio(String workspaceId) {
        var workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new EduResourceNotFoundException("Workspace not found: " + workspaceId));
        
        var dto = new com.talentboozt.s_backend.domains.edu.dto.profile.EnterprisePortfolioDTO();
        dto.setWorkspaceId(workspaceId);
        dto.setName(workspace.getName());
        dto.setDescription(workspace.getDescription());
        dto.setLogoUrl(workspace.getLogoUrl());
        dto.setType(workspace.getType());
        
        var courses = courseRepository.findByWorkspaceId(workspaceId);
        dto.setOfferedPrograms(courses.stream()
                .filter(c -> Boolean.TRUE.equals(c.getPublished()))
                .limit(8)
                .map(c -> com.talentboozt.s_backend.domains.edu.dto.profile.PortfolioCourseDTO.builder()
                        .id(c.getId()).title(c.getTitle()).thumbnail(c.getThumbnail()).category(c.getCategories() != null && c.getCategories().length > 0 ? c.getCategories()[0] : "Enterprise")
                        .rating(c.getRating()).totalStudents(c.getTotalEnrollments()).price(c.getPrice()).build())
                .toList());
        
        dto.setMetrics(Map.of(
            "totalMembers", workspace.getTotalMembers(),
            "totalCourses", workspace.getTotalCourses(),
            "totalLearningPaths", workspace.getTotalLearningPaths()
        ));
        
        return dto;
    }

    private static String extensionForContentType(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "image/gif" -> ".gif";
            default -> throw new EduBadRequestException("Unsupported image type: " + contentType);
        };
    }
}
