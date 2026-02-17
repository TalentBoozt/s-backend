package com.talentboozt.s_backend.domains.community.service;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.talentboozt.s_backend.domains.community.dto.CommunityDTO;
import com.talentboozt.s_backend.domains.community.model.CommunityMember;

public interface CommunityService {
    Page<CommunityDTO> getPaginatedCommunities(Pageable pageable, String userId);

    List<CommunityDTO> getAllCommunities();

    CommunityDTO getCommunityById(String id);

    CommunityDTO getCommunityById(String id, String userId);

    CommunityDTO createCommunity(CommunityDTO communityDTO);

    CommunityDTO updateCommunity(String id, CommunityDTO communityDTO);

    void deleteCommunity(String id);

    CommunityDTO joinCommunity(String id, String userId);

    CommunityDTO leaveCommunity(String id, String userId);

    List<CommunityMember> getCommunityMembers(String communityId);

    CommunityMember updateMemberRole(String communityId, String userId, String role);

    void removeMember(String communityId, String userId);

    List<CommunityDTO> getUserCommunities(String userId);

    Map<String, Object> getCommunityStats(String communityId);

    void banMember(String communityId, String userId, String reason);

    void unbanMember(String communityId, String userId);
}
