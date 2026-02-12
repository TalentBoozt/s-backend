package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.plat_courses.model.AmbassadorTaskProgressModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.mongodb.TaskProgressRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class AmbassadorTaskProgressService {

    @Autowired
    TaskProgressRepository taskProgressRepository;

    public AmbassadorTaskProgressModel addTaskProgress(AmbassadorTaskProgressModel taskProgress) {
        return taskProgressRepository.save(Objects.requireNonNull(taskProgress));
    }

    public Iterable<AmbassadorTaskProgressModel> getAllTaskProgress() {
        return taskProgressRepository.findAll();
    }

    public AmbassadorTaskProgressModel getTaskProgressById(String id) {
        return taskProgressRepository.findById(Objects.requireNonNull(id)).orElse(null);
    }

    public AmbassadorTaskProgressModel rewardProgress(String progressId) {
        Optional<AmbassadorTaskProgressModel> progress = taskProgressRepository.findById(Objects.requireNonNull(progressId));
        if (progress.isPresent()) {
            AmbassadorTaskProgressModel p = progress.get();
            p.setRewardStatus("ISSUED");
            return taskProgressRepository.save(p);
        }
        return null;
    }
}
