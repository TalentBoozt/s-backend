package com.talentboozt.s_backend.domains.community.service;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.stereotype.Service;

import com.talentboozt.s_backend.domains.community.dto.CommunityDTO;
import com.talentboozt.s_backend.domains.community.model.Community;
import com.talentboozt.s_backend.domains.community.model.CommunityMember;
import com.talentboozt.s_backend.domains.community.repository.mongodb.CommunityRepository;
import com.talentboozt.s_backend.domains.community.repository.mongodb.CommunityMemberRepository;
import com.talentboozt.s_backend.domains.community.exception.AccessDeniedException;
import com.talentboozt.s_backend.domains.community.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final NotificationService notificationService;

    @Override
    public org.springframework.data.domain.Page<CommunityDTO> getPaginatedCommunities(
            org.springframework.data.domain.Pageable pageable) {
        return communityRepository.findAll(pageable)
                .map(community -> mapToDTO(community, null));
    }

    @Override
    public List<CommunityDTO> getAllCommunities() {
        return communityRepository.findAll().stream()
                .map(community -> mapToDTO(community, null))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "communities", key = "#id")
    public CommunityDTO getCommunityById(String id) {
        return getCommunityById(id, null);
    }

    @Override
    public CommunityDTO getCommunityById(String id, String userId) {
        return communityRepository.findById(Objects.requireNonNull(id))
                .map(community -> mapToDTO(community, userId))
                .orElseThrow(() -> new ResourceNotFoundException("Community", "id", id));
    }

    @Override
    public CommunityDTO createCommunity(CommunityDTO communityDTO) {
        Community community = Community.builder()
                .name(communityDTO.getName())
                .description(communityDTO.getDescription())
                .icon(communityDTO.getIcon())
                .bannerImage(communityDTO.getBannerImage())
                .creatorId(communityDTO.getCreatorId())
                .adminIds(new ArrayList<>(List.of(communityDTO.getCreatorId())))
                .moderatorIds(new ArrayList<>())
                .privacy(Community.CommunityPrivacy.valueOf(
                        communityDTO.getPrivacy() != null ? communityDTO.getPrivacy() : "PUBLIC"))
                .createdAt(LocalDateTime.now())
                .rules(communityDTO.getRules())
                .category(communityDTO.getCategory())
                .tags(communityDTO.getTags() != null ? communityDTO.getTags() : new ArrayList<>())
                .isVerified(false)
                .build();

        Community savedCommunity = communityRepository.save(Objects.requireNonNull(community));

        // Auto-join creator as admin
        CommunityMember creatorMember = CommunityMember.builder()
                .communityId(savedCommunity.getId())
                .userId(communityDTO.getCreatorId())
                .role(CommunityMember.MemberRole.ADMIN)
                .joinedAt(LocalDateTime.now())
                .isBanned(false)
                .build();
        communityMemberRepository.save(Objects.requireNonNull(creatorMember));

        return mapToDTO(savedCommunity, communityDTO.getCreatorId());
    }

    @Override
    @CacheEvict(value = "communities", key = "#id")
    public void deleteCommunity(String id) {
        // Delete all members first
        List<CommunityMember> members = communityMemberRepository.findByCommunityId(id);
        members.forEach(member -> communityMemberRepository.deleteById(Objects.requireNonNull(member.getId())));

        // Delete community
        communityRepository.deleteById(Objects.requireNonNull(id));
    }

    @Override
    @CachePut(value = "communities", key = "#id")
    public CommunityDTO updateCommunity(String id, CommunityDTO communityDTO) {
        Community community = communityRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Community", "id", id));

        // Update fields
        if (communityDTO.getName() != null)
            community.setName(communityDTO.getName());
        if (communityDTO.getDescription() != null)
            community.setDescription(communityDTO.getDescription());
        if (communityDTO.getIcon() != null)
            community.setIcon(communityDTO.getIcon());
        if (communityDTO.getBannerImage() != null)
            community.setBannerImage(communityDTO.getBannerImage());
        if (communityDTO.getPrivacy() != null) {
            community.setPrivacy(Community.CommunityPrivacy.valueOf(communityDTO.getPrivacy()));
        }
        if (communityDTO.getCategory() != null)
            community.setCategory(communityDTO.getCategory());
        if (communityDTO.getTags() != null)
            community.setTags(communityDTO.getTags());
        if (communityDTO.getRules() != null)
            community.setRules(communityDTO.getRules());

        community.setUpdatedAt(LocalDateTime.now());

        Community updated = communityRepository.save(community);
        return mapToDTO(updated, null);
    }

    @Override
    public List<CommunityMember> getCommunityMembers(String communityId) {
        return communityMemberRepository.findByCommunityId(communityId);
    }

    @Override
    public CommunityMember updateMemberRole(String communityId, String userId, String role) {
        CommunityMember member = communityMemberRepository.findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Community member not found for user: " + userId));

        member.setRole(CommunityMember.MemberRole.valueOf(role));
        return communityMemberRepository.save(member);
    }

    @Override
    public void removeMember(String communityId, String userId) {
        communityMemberRepository.deleteByCommunityIdAndUserId(communityId, userId);
    }

    @Override
    public List<CommunityDTO> getUserCommunities(String userId) {
        List<CommunityMember> memberships = communityMemberRepository.findByUserId(userId);
        return memberships.stream()
                .map(member -> communityRepository.findById(Objects.requireNonNull(member.getCommunityId())))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .map(community -> mapToDTO(community, userId))
                .collect(Collectors.toList());
    }

    @Override
    public java.util.Map<String, Object> getCommunityStats(String communityId) {
        long memberCount = communityMemberRepository.countByCommunityId(communityId);

        // Mock stats for now - can be enhanced with actual post/comment counting
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalMembers", memberCount);
        stats.put("totalPosts", 0); // TODO: Implement post counting
        stats.put("totalComments", 0); // TODO: Implement comment counting
        stats.put("totalReactions", 0); // TODO: Implement reaction counting
        stats.put("engagementRate", 0.0); // TODO: Calculate engagement
        stats.put("memberGrowth", new ArrayList<>()); // TODO: Implement growth tracking
        stats.put("topContributors", new ArrayList<>()); // TODO: Implement contributor tracking

        return stats;
    }

    @Override
    public CommunityDTO joinCommunity(String id, String userId) {
        Community community = communityRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Community", "id", id));

        // Check if already a member
        if (communityMemberRepository.existsByCommunityIdAndUserId(id, userId)) {
            return mapToDTO(community, userId);
        }

        // Create membership
        CommunityMember member = CommunityMember.builder()
                .communityId(id)
                .userId(userId)
                .role(CommunityMember.MemberRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .isBanned(false)
                .build();
        communityMemberRepository.save(Objects.requireNonNull(member));

        // Notify admins
        if (community.getAdminIds() != null) {
            for (String adminId : community.getAdminIds()) {
                if (!adminId.equals(userId)) {
                    notificationService.createNotification(
                            adminId, userId,
                            com.talentboozt.s_backend.domains.community.model.Notification.NotificationType.FOLLOW,
                            id);
                }
            }
        }

        return mapToDTO(community, userId);
    }

    @Override
    public CommunityDTO leaveCommunity(String id, String userId) {
        Community community = communityRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Community", "id", id));

        // Delete membership
        communityMemberRepository.deleteByCommunityIdAndUserId(id, userId);

        // Remove from admin/moderator lists if present
        if (community.getAdminIds() != null && community.getAdminIds().contains(userId)) {
            community.getAdminIds().remove(userId);
            communityRepository.save(community);
        }
        if (community.getModeratorIds() != null && community.getModeratorIds().contains(userId)) {
            community.getModeratorIds().remove(userId);
            communityRepository.save(community);
        }

        return mapToDTO(community, userId);
    }

    @Override
    public void banMember(String communityId, String userId, String reason) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community", "id", communityId));

        // Check if user is a member
        CommunityMember member = communityMemberRepository.findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("CommunityMember", "userId", userId));

        // Check if the caller has permission (admin or moderator)
        if (!isCommunityAdmin(communityId, userId) && !isCommunityModerator(communityId, userId)) {
            throw new AccessDeniedException("You don't have permission to ban members from this community");
        }

        // Check if trying to ban an admin
        if (isCommunityAdmin(communityId, userId)) {
            throw new IllegalArgumentException("Cannot ban community admins");
        }

        // Ban the member
        member.setBanned(true);
        member.setBannedAt(LocalDateTime.now());
        member.setBannedReason(reason);
        communityMemberRepository.save(member);

        // Remove from moderator list if they were a moderator
        if (community.getModeratorIds() != null && community.getModeratorIds().contains(userId)) {
            community.getModeratorIds().remove(userId);
            communityRepository.save(community);
        }

        // Notify the banned user
        notificationService.createNotification(
                userId, communityId,
                com.talentboozt.s_backend.domains.community.model.Notification.NotificationType.BAN,
                reason);
    }

    @Override
    public void unbanMember(String communityId, String userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community", "id", communityId));

        // Check if user is a member
        CommunityMember member = communityMemberRepository.findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("CommunityMember", "userId", userId));

        // Check if the caller has permission (admin or moderator)
        if (!isCommunityAdmin(communityId, userId) && !isCommunityModerator(communityId, userId)) {
            throw new AccessDeniedException("You don't have permission to unban members from this community");
        }

        // Unban the member
        member.setBanned(false);
        member.setBannedAt(null);
        member.setBannedReason(null);
        communityMemberRepository.save(member);

        // Notify the unbanned user
        notificationService.createNotification(
                userId, communityId,
                com.talentboozt.s_backend.domains.community.model.Notification.NotificationType.UNBAN,
                null);
    }

    private boolean isCommunityAdmin(String communityId, String userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community", "id", communityId));
        return community.getAdminIds() != null && community.getAdminIds().contains(userId);
    }

    private boolean isCommunityModerator(String communityId, String userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community", "id", communityId));
        return community.getModeratorIds() != null && community.getModeratorIds().contains(userId);
    }

    private CommunityDTO mapToDTO(Community community, String userId) {
        long memberCount = communityMemberRepository.countByCommunityId(community.getId());
        boolean isJoined = false;
        String userRole = null;

        if (userId != null) {
            var memberOpt = communityMemberRepository.findByCommunityIdAndUserId(
                    community.getId(), userId);
            if (memberOpt.isPresent()) {
                isJoined = true;
                userRole = memberOpt.get().getRole().name();
            }
        }

        return CommunityDTO.builder()
                .id(community.getId())
                .name(community.getName())
                .description(community.getDescription())
                .icon(community.getIcon())
                .bannerImage(community.getBannerImage())
                .creatorId(community.getCreatorId())
                .adminIds(community.getAdminIds())
                .moderatorIds(community.getModeratorIds())
                .privacy(community.getPrivacy() != null ? community.getPrivacy().name() : "PUBLIC")
                .createdAt(community.getCreatedAt() != null
                        ? community.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME)
                        : null)
                .updatedAt(community.getUpdatedAt() != null
                        ? community.getUpdatedAt().format(DateTimeFormatter.ISO_DATE_TIME)
                        : null)
                .rules(community.getRules())
                .category(community.getCategory())
                .tags(community.getTags())
                .isVerified(community.isVerified())
                .memberCount(memberCount)
                .isJoined(isJoined)
                .userRole(userRole)
                .build();
    }
}
