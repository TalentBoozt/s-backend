package com.talentboozt.s_backend.domains.community.service;

import java.util.List;

import com.talentboozt.s_backend.domains.community.dto.CommunityDTO;

public interface CommunityService {
    List<CommunityDTO> getAllCommunities();

    CommunityDTO getCommunityById(String id);

    CommunityDTO createCommunity(CommunityDTO communityDTO);

    void deleteCommunity(String id);
}
