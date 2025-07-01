package com.talentboozt.s_backend.Repository.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorLeaderboardModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmbassadorLeaderboardRepository extends MongoRepository<AmbassadorLeaderboardModel, String> {
    List<AmbassadorLeaderboardModel> findByTypeOrderByRankAsc(String type);
}
