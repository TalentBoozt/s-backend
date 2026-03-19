package com.talentboozt.s_backend.domains.lifeplanner.planner.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.talentboozt.s_backend.domains.lifeplanner.planner.model.DailySchedule;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface DailyScheduleRepository extends MongoRepository<DailySchedule, String> {
    Optional<DailySchedule> findByUserIdAndScheduleDate(String userId, LocalDate scheduleDate);
    List<DailySchedule> findByPlanId(String planId);
    void deleteByPlanId(String planId);
    List<DailySchedule> findByUserId(String userId);
}
