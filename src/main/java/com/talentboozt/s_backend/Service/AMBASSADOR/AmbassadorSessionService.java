package com.talentboozt.s_backend.Service.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorProfileModel;
import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorSessionModel;
import com.talentboozt.s_backend.Repository.AMBASSADOR.AmbassadorProfileRepository;
import com.talentboozt.s_backend.Repository.AMBASSADOR.AmbassadorSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AmbassadorSessionService {

    @Autowired
    AmbassadorSessionRepository ambassadorSessionRepository;

    @Autowired
    AmbassadorProfileRepository ambassadorProfileRepository;

    public AmbassadorSessionModel addAmbassadorSession(String id, AmbassadorSessionModel ambassadorSessionModel) {
        Optional<AmbassadorProfileModel> ambassadorProfileModel = ambassadorProfileRepository.findById(id);
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
        return ambassadorSessionRepository.findById(id).orElse(null);
    }

    public AmbassadorSessionModel updateAmbassadorSession(String id, AmbassadorSessionModel ambassadorSessionModel) {
        Optional<AmbassadorSessionModel> ambassadorSessionModel1 = ambassadorSessionRepository.findById(id);
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
        ambassadorSessionRepository.deleteById(id);
    }
}
