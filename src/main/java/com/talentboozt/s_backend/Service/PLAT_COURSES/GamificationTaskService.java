package com.talentboozt.s_backend.Service.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.GamificationTaskModel;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.GamificationTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GamificationTaskService {

    @Autowired
    private GamificationTaskRepository gamificationTaskRepository;

    public GamificationTaskModel addTask(GamificationTaskModel task) {
        return gamificationTaskRepository.save(task);
    }

    public Iterable<GamificationTaskModel> getAllTasks() {
        return gamificationTaskRepository.findAll();
    }

    public GamificationTaskModel getTaskById(String id) {
        return gamificationTaskRepository.findById(id).orElse(null);
    }

    public void deleteTask(String id) {
        gamificationTaskRepository.deleteById(id);
    }

    public GamificationTaskModel updateTask(GamificationTaskModel task) {
        Optional<GamificationTaskModel> gamificationTaskModel = gamificationTaskRepository.findById(task.getId());
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
