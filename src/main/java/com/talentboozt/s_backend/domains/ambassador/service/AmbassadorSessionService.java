package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorSessionModel;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorProfileRepository;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class AmbassadorSessionService {

    @Autowired
    AmbassadorSessionRepository ambassadorSessionRepository;

    @Autowired
    AmbassadorProfileRepository ambassadorProfileRepository;

    public AmbassadorSessionModel addAmbassadorSession(String id, AmbassadorSessionModel ambassadorSessionModel) {
        Optional<AmbassadorProfileModel> ambassadorProfileModel = ambassadorProfileRepository.findById(Objects.requireNonNull(id));
        if (ambassadorProfileModel.isPresent()) {
            ambassadorSessionModel.setAmbassadorId(id);
            return ambassadorSessionRepository.save(ambassadorSessionModel);
        }
        return null;
    }

    public Iterable<AmbassadorSessionModel> getAmbassadorSessions(String id) {
        return ambassadorSessionRepository.findByAmbassadorId(id);
    }

    public AmbassadorSessionModel getAmbassadorSession(String id) {
        return ambassadorSessionRepository.findById(Objects.requireNonNull(id)).orElse(null);
    }

    public AmbassadorSessionModel updateAmbassadorSession(String id, AmbassadorSessionModel ambassadorSessionModel) {
        Optional<AmbassadorSessionModel> ambassadorSessionModel1 = ambassadorSessionRepository.findById(Objects.requireNonNull(id));
        if (ambassadorSessionModel1.isPresent()) {
            AmbassadorSessionModel newAmbassadorSessionModel = ambassadorSessionModel1.get();
            newAmbassadorSessionModel.setType(ambassadorSessionModel.getType());
            newAmbassadorSessionModel.setTopic(ambassadorSessionModel.getTopic());
            newAmbassadorSessionModel.setSessionLink(ambassadorSessionModel.getSessionLink());
            newAmbassadorSessionModel.setDate(ambassadorSessionModel.getDate());
            newAmbassadorSessionModel.setAttendeeCount(ambassadorSessionModel.getAttendeeCount());
            newAmbassadorSessionModel.setCompleted(ambassadorSessionModel.isCompleted());
            return ambassadorSessionRepository.save(newAmbassadorSessionModel);
        }
        return null;
    }

    public void deleteAmbassadorSession(String id) {
        ambassadorSessionRepository.deleteById(Objects.requireNonNull(id));
    }
}
