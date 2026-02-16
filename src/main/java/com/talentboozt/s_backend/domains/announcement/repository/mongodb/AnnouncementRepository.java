package com.talentboozt.s_backend.domains.announcement.repository.mongodb;

import com.talentboozt.s_backend.domains.announcement.model.Announcement;
import com.talentboozt.s_backend.domains.announcement.model.AnnouncementPriority;
import com.talentboozt.s_backend.domains.announcement.model.AnnouncementStatus;
import com.talentboozt.s_backend.domains.announcement.model.AnnouncementType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends MongoRepository<Announcement, String> {
    Optional<Announcement> findBySlug(String slug);

    @Query("{ 'status': 'PUBLISHED', 'publishedAt': { $lte: ?0 }, $or: [ { 'expiresAt': null }, { 'expiresAt': { $gte: ?0 } } ] }")
    List<Announcement> findActiveAnnouncements(LocalDateTime now);

    List<Announcement> findByStatus(AnnouncementStatus status);

    List<Announcement> findByType(AnnouncementType type);

    List<Announcement> findByPriority(AnnouncementPriority priority);
}
