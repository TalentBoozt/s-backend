package com.talentboozt.s_backend.domains.ambassador.repository;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorLeaderboardModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmbassadorLeaderboardRepository extends MongoRepository<AmbassadorLeaderboardModel, String> {
    List<AmbassadorLeaderboardModel> findByTypeOrderByRankAsc(String type);
}
