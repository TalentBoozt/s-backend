package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.plat_courses.model.GamificationTaskModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.GamificationTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class GamificationTaskService {

    @Autowired
    private GamificationTaskRepository gamificationTaskRepository;

    public GamificationTaskModel addTask(GamificationTaskModel task) {
        return gamificationTaskRepository.save(Objects.requireNonNull(task));
    }

    public Iterable<GamificationTaskModel> getAllTasks() {
        return gamificationTaskRepository.findAll();
    }

    public GamificationTaskModel getTaskById(String id) {
        return gamificationTaskRepository.findById(Objects.requireNonNull(id)).orElse(null);
    }

    public void deleteTask(String id) {
        gamificationTaskRepository.deleteById(Objects.requireNonNull(id));
    }

    public GamificationTaskModel updateTask(GamificationTaskModel task) {
        Optional<GamificationTaskModel> gamificationTaskModel = gamificationTaskRepository.findById(Objects.requireNonNull(task.getId()));
        if (gamificationTaskModel.isPresent()) {
            GamificationTaskModel existingTask = gamificationTaskModel.get();
            existingTask.setTitle(task.getTitle());
            existingTask.setDescription(task.getDescription());
            existingTask.setType(task.getType());
            existingTask.setLevel(task.getLevel());
            existingTask.setTargetValue(task.getTargetValue());
            existingTask.setRewardType(task.getRewardType());
            existingTask.setRewardId(task.getRewardId());
            existingTask.setRecurring(task.isRecurring());
            existingTask.setFrequencyInDays(task.getFrequencyInDays());
            return gamificationTaskRepository.save(existingTask);
        } else {
            return null;
        }
    }
}
