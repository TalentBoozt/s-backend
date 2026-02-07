package com.talentboozt.s_backend.domains.community.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.talentboozt.s_backend.domains.community.dto.CommunityDTO;
import com.talentboozt.s_backend.domains.community.model.Community;
import com.talentboozt.s_backend.domains.community.repository.CommunityRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;

    @Override
    public List<CommunityDTO> getAllCommunities() {
        return communityRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommunityDTO getCommunityById(String id) {
        return communityRepository.findById(Objects.requireNonNull(id))
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Community not found with id: " + id));
    }

    @Override
    public CommunityDTO createCommunity(CommunityDTO communityDTO) {
        Community community = Community.builder()
                .name(communityDTO.getName())
                .description(communityDTO.getDescription())
                .icon(communityDTO.getIcon())
                .memberCount(0)
                .isJoined(false)
                .build();

        Community savedCommunity = communityRepository.save(Objects.requireNonNull(community));
        return mapToDTO(savedCommunity);
    }

    @Override
    public void deleteCommunity(String id) {
        communityRepository.deleteById(Objects.requireNonNull(id));
    }

    private CommunityDTO mapToDTO(Community community) {
        return CommunityDTO.builder()
                .id(community.getId())
                .name(community.getName())
                .description(community.getDescription())
                .icon(community.getIcon())
                .memberCount(community.getMemberCount())
                .isJoined(community.isJoined())
                .build();
    }
}
