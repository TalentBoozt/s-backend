package com.talentboozt.s_backend.Service.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.AmbassadorTaskProgressModel;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.TaskProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AmbassadorTaskProgressService {

    @Autowired
    TaskProgressRepository taskProgressRepository;

    public AmbassadorTaskProgressModel addTaskProgress(AmbassadorTaskProgressModel taskProgress) {
        return taskProgressRepository.save(taskProgress);
    }

    public Iterable<AmbassadorTaskProgressModel> getAllTaskProgress() {
        return taskProgressRepository.findAll();
    }

    public AmbassadorTaskProgressModel getTaskProgressById(String id) {
        return taskProgressRepository.findById(id).orElse(null);
    }

    public AmbassadorTaskProgressModel rewardProgress(String progressId) {
        Optional<AmbassadorTaskProgressModel> progress = taskProgressRepository.findById(progressId);
        if (progress.isPresent()) {
            AmbassadorTaskProgressModel p = progress.get();
            p.setRewardStatus("ISSUED");
            return taskProgressRepository.save(p);
        }
        return null;
    }
}
