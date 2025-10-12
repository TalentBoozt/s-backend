package com.talentboozt.s_backend.domains.user.service;

import com.talentboozt.s_backend.domains.user.model.TrainerProfile;
import com.talentboozt.s_backend.domains.user.repository.TrainerProfileRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
        Optional<TrainerProfile> trainerProfileOpt = Optional.ofNullable(trainerProfileRepository.findByEmployeeId(employeeId));

        if (trainerProfileOpt.isPresent()) {
            TrainerProfile existsProfile = trainerProfileOpt.get();
            existsProfile.setEmployeeId(trainerProfile.getEmployeeId());
            existsProfile.setHeadline(trainerProfile.getHeadline());
            existsProfile.setBio(trainerProfile.getBio());
            existsProfile.setSpecialties(trainerProfile.getSpecialties());
            existsProfile.setLanguages(trainerProfile.getLanguages());
            existsProfile.setHourlyRate(trainerProfile.getHourlyRate());
            existsProfile.setAvailability(trainerProfile.getAvailability());
            existsProfile.setCertifications(trainerProfile.getCertifications());
            existsProfile.setRating(trainerProfile.getRating());
            existsProfile.setTotalReviews(trainerProfile.getTotalReviews());
            existsProfile.setTrainerVideoIntro(trainerProfile.getTrainerVideoIntro());
            existsProfile.setWebsite(trainerProfile.getWebsite());
            existsProfile.setYoutube(trainerProfile.getYoutube());
            existsProfile.setLinkedIn(trainerProfile.getLinkedIn());
            existsProfile.setPublicProfile(trainerProfile.isPublicProfile());

            return trainerProfileRepository.save(existsProfile);
        } else {
            return trainerProfileRepository.save(trainerProfile);
        }
    }

    @Async
    public CompletableFuture<TrainerProfile> getTrainerByEmpIdAsync(String id) {
        TrainerProfile trainerProfile = getByEmpId(id);
        return CompletableFuture.completedFuture(trainerProfile);
    }
}
