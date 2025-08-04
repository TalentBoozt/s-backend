package com.talentboozt.s_backend.domains.ambassador.controller;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorSessionModel;
import com.talentboozt.s_backend.domains.ambassador.service.AmbassadorSessionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmbassadorSessionControllerTest {

    @Mock
    private AmbassadorSessionService ambassadorSessionService;

    @InjectMocks
    private AmbassadorSessionController ambassadorSessionController;

    @Test
    void addAmbassadorSessionReturnsCreatedSession() {
        String ambassadorId = "amb123";
        AmbassadorSessionModel sessionModel = new AmbassadorSessionModel();
        AmbassadorSessionModel createdSession = new AmbassadorSessionModel();
        createdSession.setId("session123");
        when(ambassadorSessionService.addAmbassadorSession(ambassadorId, sessionModel)).thenReturn(createdSession);

        AmbassadorSessionModel result = ambassadorSessionController.addAmbassadorSession(ambassadorId, sessionModel);

        assertEquals(createdSession, result);
    }

    @Test
    void getAmbassadorSessionsReturnsListForValidAmbassadorId() {
        String ambassadorId = "amb123";
        List<AmbassadorSessionModel> sessions = List.of(new AmbassadorSessionModel(), new AmbassadorSessionModel());
        when(ambassadorSessionService.getAmbassadorSessions(ambassadorId)).thenReturn(sessions);

        Iterable<AmbassadorSessionModel> result = ambassadorSessionController.getAmbassadorSessions(ambassadorId);

        assertEquals(sessions, result);
    }

    @Test
    void getAmbassadorSessionReturnsSessionForValidId() {
        String sessionId = "session123";
        AmbassadorSessionModel session = new AmbassadorSessionModel();
        session.setId(sessionId);
        when(ambassadorSessionService.getAmbassadorSession(sessionId)).thenReturn(session);

        AmbassadorSessionModel result = ambassadorSessionController.getAmbassadorSession(sessionId);

        assertEquals(session, result);
    }

    @Test
    void updateAmbassadorSessionUpdatesSuccessfully() {
        String sessionId = "session123";
        AmbassadorSessionModel sessionModel = new AmbassadorSessionModel();
        AmbassadorSessionModel updatedSession = new AmbassadorSessionModel();
        updatedSession.setId(sessionId);
        when(ambassadorSessionService.updateAmbassadorSession(sessionId, sessionModel)).thenReturn(updatedSession);

        AmbassadorSessionModel result = ambassadorSessionController.updateAmbassadorSession(sessionId, sessionModel);

        assertEquals(updatedSession, result);
    }

    @Test
    void deleteAmbassadorSessionDeletesSuccessfully() {
        String sessionId = "session123";

        ambassadorSessionController.deleteAmbassadorSession(sessionId);

        verify(ambassadorSessionService).deleteAmbassadorSession(sessionId);
    }
}
