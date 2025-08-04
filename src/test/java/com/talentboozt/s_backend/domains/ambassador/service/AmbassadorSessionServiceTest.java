package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorSessionModel;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorProfileRepository;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorSessionRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmbassadorSessionServiceTest {

    @Mock
    private AmbassadorSessionRepository ambassadorSessionRepository;

    @Mock
    private AmbassadorProfileRepository ambassadorProfileRepository;

    @InjectMocks
    private AmbassadorSessionService ambassadorSessionService;

    @Test
    void addAmbassadorSession_savesSessionWhenAmbassadorExists() {
        String ambassadorId = "123";
        AmbassadorSessionModel session = new AmbassadorSessionModel();
        session.setType("Workshop");

        when(ambassadorProfileRepository.findById(ambassadorId)).thenReturn(Optional.of(new AmbassadorProfileModel()));
        when(ambassadorSessionRepository.save(any(AmbassadorSessionModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AmbassadorSessionModel result = ambassadorSessionService.addAmbassadorSession(ambassadorId, session);

        assertNotNull(result);
        assertEquals(ambassadorId, result.getAmbassadorId());
        verify(ambassadorSessionRepository).save(session);
    }

    @Test
    void addAmbassadorSession_returnsNullWhenAmbassadorDoesNotExist() {
        String ambassadorId = "123";
        AmbassadorSessionModel session = new AmbassadorSessionModel();

        when(ambassadorProfileRepository.findById(ambassadorId)).thenReturn(Optional.empty());

        AmbassadorSessionModel result = ambassadorSessionService.addAmbassadorSession(ambassadorId, session);

        assertNull(result);
        verify(ambassadorSessionRepository, never()).save(any(AmbassadorSessionModel.class));
    }

    @Test
    void getAmbassadorSessions_returnsSessionsForGivenAmbassadorId() {
        String ambassadorId = "123";
        List<AmbassadorSessionModel> sessions = List.of(new AmbassadorSessionModel(), new AmbassadorSessionModel());

        when(ambassadorSessionRepository.findByAmbassadorId(ambassadorId)).thenReturn(sessions);

        Iterable<AmbassadorSessionModel> result = ambassadorSessionService.getAmbassadorSessions(ambassadorId);

        assertNotNull(result);
        assertEquals(2, ((List<AmbassadorSessionModel>) result).size());
        verify(ambassadorSessionRepository).findByAmbassadorId(ambassadorId);
    }

    @Test
    void getAmbassadorSession_returnsSessionWhenExists() {
        String sessionId = "session1";
        AmbassadorSessionModel session = new AmbassadorSessionModel();
        session.setId(sessionId);

        when(ambassadorSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        AmbassadorSessionModel result = ambassadorSessionService.getAmbassadorSession(sessionId);

        assertNotNull(result);
        assertEquals(sessionId, result.getId());
        verify(ambassadorSessionRepository).findById(sessionId);
    }

    @Test
    void getAmbassadorSession_returnsNullWhenSessionDoesNotExist() {
        String sessionId = "session1";

        when(ambassadorSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        AmbassadorSessionModel result = ambassadorSessionService.getAmbassadorSession(sessionId);

        assertNull(result);
        verify(ambassadorSessionRepository).findById(sessionId);
    }

    @Test
    void updateAmbassadorSession_updatesSessionWhenExists() {
        String sessionId = "session1";
        AmbassadorSessionModel existingSession = new AmbassadorSessionModel();
        existingSession.setId(sessionId);
        existingSession.setType("Workshop");

        AmbassadorSessionModel updatedSession = new AmbassadorSessionModel();
        updatedSession.setType("Webinar");

        when(ambassadorSessionRepository.findById(sessionId)).thenReturn(Optional.of(existingSession));
        when(ambassadorSessionRepository.save(any(AmbassadorSessionModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AmbassadorSessionModel result = ambassadorSessionService.updateAmbassadorSession(sessionId, updatedSession);

        assertNotNull(result);
        assertEquals("Webinar", result.getType());
        verify(ambassadorSessionRepository).save(existingSession);
    }

    @Test
    void updateAmbassadorSession_returnsNullWhenSessionDoesNotExist() {
        String sessionId = "session1";
        AmbassadorSessionModel updatedSession = new AmbassadorSessionModel();

        when(ambassadorSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        AmbassadorSessionModel result = ambassadorSessionService.updateAmbassadorSession(sessionId, updatedSession);

        assertNull(result);
        verify(ambassadorSessionRepository, never()).save(any(AmbassadorSessionModel.class));
    }

    @Test
    void deleteAmbassadorSession_deletesSessionWhenExists() {
        String sessionId = "session1";

        doNothing().when(ambassadorSessionRepository).deleteById(sessionId);

        ambassadorSessionService.deleteAmbassadorSession(sessionId);

        verify(ambassadorSessionRepository).deleteById(sessionId);
    }
}
