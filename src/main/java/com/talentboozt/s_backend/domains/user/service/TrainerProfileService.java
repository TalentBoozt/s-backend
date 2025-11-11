package com.talentboozt.s_backend.domains.user.service;

import com.talentboozt.s_backend.domains.user.model.TrainerProfile;
import com.talentboozt.s_backend.domains.user.repository.TrainerProfileRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TrainerProfileService {

    private final TrainerProfileRepository trainerProfileRepository;

    public TrainerProfileService(TrainerProfileRepository trainerProfileRepository) {
        this.trainerProfileRepository = trainerProfileRepository;
    }

    public TrainerProfile getByEmpId(String employeeId) {
        return trainerProfileRepository.findByEmployeeId(employeeId);
    }

    public TrainerProfile updateByEmployeeId(String employeeId, TrainerProfile trainerProfile) {
        TrainerProfile existing = trainerProfileRepository.findByEmployeeId(employeeId);

        if (existing != null) {
            existing.setHeadline(trainerProfile.getHeadline());
            existing.setBio(trainerProfile.getBio());
            existing.setSpecialties(trainerProfile.getSpecialties());
            existing.setLanguages(trainerProfile.getLanguages());
            existing.setHourlyRate(trainerProfile.getHourlyRate());
            existing.setAvailability(trainerProfile.getAvailability());
            existing.setCertifications(trainerProfile.getCertifications());
            existing.setRating(trainerProfile.getRating());
            existing.setTotalReviews(trainerProfile.getTotalReviews());
            existing.setTrainerVideoIntro(trainerProfile.getTrainerVideoIntro());
            existing.setWebsite(trainerProfile.getWebsite());
            existing.setYoutube(trainerProfile.getYoutube());
            existing.setLinkedIn(trainerProfile.getLinkedIn());
            existing.setPublicProfile(trainerProfile.isPublicProfile());
            return trainerProfileRepository.save(existing);
        }

        // for new trainer
        trainerProfile.setEmployeeId(employeeId);
        return trainerProfileRepository.save(trainerProfile);
    }

    @Async
    public CompletableFuture<TrainerProfile> getTrainerByEmpIdAsync(String id) {
        TrainerProfile trainerProfile = getByEmpId(id);
        return CompletableFuture.completedFuture(trainerProfile);
    }
}
