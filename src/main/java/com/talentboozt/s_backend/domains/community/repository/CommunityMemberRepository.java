package com.talentboozt.s_backend.domains.community.repository;

import com.talentboozt.s_backend.domains.community.model.CommunityMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityMemberRepository extends MongoRepository<CommunityMember, String> {
    List<CommunityMember> findByCommunityId(String communityId);

    List<CommunityMember> findByUserId(String userId);

    Optional<CommunityMember> findByCommunityIdAndUserId(String communityId, String userId);

    long countByCommunityId(String communityId);

    boolean existsByCommunityIdAndUserId(String communityId, String userId);

    void deleteByCommunityIdAndUserId(String communityId, String userId);
}
