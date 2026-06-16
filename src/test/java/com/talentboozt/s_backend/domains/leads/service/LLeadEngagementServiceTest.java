package com.talentboozt.s_backend.domains.leads.service;

import com.talentboozt.s_backend.domains.leads.crm.model.LLead;
import com.talentboozt.s_backend.domains.leads.crm.repository.LLeadRepository;
import com.talentboozt.s_backend.domains.leads.model.LRawSignal;
import com.talentboozt.s_backend.domains.leads.repository.LRawSignalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LLeadEngagementServiceTest {

    private LAIService aiService;
    private LLeadRepository leadRepository;
    private LRawSignalRepository rawSignalRepository;

    private LLeadEngagementService service;

    @BeforeEach
    void setUp() {
        aiService = mock(LAIService.class);
        leadRepository = mock(LLeadRepository.class);
        rawSignalRepository = mock(LRawSignalRepository.class);

        service = new LLeadEngagementService(aiService, leadRepository, rawSignalRepository);
    }

    @Test
    void generateDraft_shouldReturnAiReplyWhenSignalExists() {
        // Arrange
        String signalId = "sig-123";
        String tone = "professional";
        LRawSignal signal = new LRawSignal();
        signal.setContent("Need help with Java");

        when(rawSignalRepository.findById(signalId)).thenReturn(Optional.of(signal));
        when(aiService.generateReply("Need help with Java", tone)).thenReturn("Here is a solution...");

        // Act
        String draft = service.generateDraft(signalId, tone);

        // Assert
        assertThat(draft).isEqualTo("Here is a solution...");
    }

    @Test
    void generateDraft_shouldReturnFallbackStringWhenSignalDoesNotExist() {
        // Arrange
        String signalId = "sig-123";
        when(rawSignalRepository.findById(signalId)).thenReturn(Optional.empty());

        // Act
        String draft = service.generateDraft(signalId, "professional");

        // Assert
        assertThat(draft).isEqualTo("Sorry, I couldn't find the original signal to generate a reply.");
    }

    @Test
    void executeReply_shouldUpdateLeadStatusAndAddTimelineEventWhenLeadExists() {
        // Arrange
        String signalId = "sig-123";
        LLead lead = new LLead();
        lead.setSourceSignalId(signalId);
        lead.setStatus("NEW");

        when(leadRepository.findBySourceSignalId(signalId)).thenReturn(Optional.of(lead));

        // Act
        service.executeReply(signalId, "Short reply");

        // Assert
        verify(leadRepository, times(1)).save(lead);
        assertThat(lead.getStatus()).isEqualTo("CONTACTED");
        assertThat(lead.getTimeline()).hasSize(1);
        assertThat(lead.getTimeline().get(0).getAction()).isEqualTo("REPLY_SENT");
        assertThat(lead.getTimeline().get(0).getDescription()).isEqualTo("AI-Generated reply sent: Short reply");
    }

    @Test
    void executeReply_shouldUpdateRawSignalStatusWhenLeadDoesNotExistButSignalExists() {
        // Arrange
        String signalId = "sig-123";
        LRawSignal signal = new LRawSignal();
        signal.setId(signalId);
        signal.setStatus("NEW");

        when(leadRepository.findBySourceSignalId(signalId)).thenReturn(Optional.empty());
        when(rawSignalRepository.findById(signalId)).thenReturn(Optional.of(signal));

        // Act
        service.executeReply(signalId, "Short reply");

        // Assert
        verify(rawSignalRepository, times(1)).save(signal);
        assertThat(signal.getStatus()).isEqualTo("CONTACTED");
    }

    @Test
    void executeReply_shouldDoNothingWhenNeitherLeadNorSignalExists() {
        // Arrange
        String signalId = "sig-123";
        when(leadRepository.findBySourceSignalId(signalId)).thenReturn(Optional.empty());
        when(rawSignalRepository.findById(signalId)).thenReturn(Optional.empty());

        // Act
        service.executeReply(signalId, "Short reply");

        // Assert
        verify(leadRepository, never()).save(any());
        verify(rawSignalRepository, never()).save(any());
    }

    @Test
    void executeReply_shouldTruncateLongRepliesInTimelineEvent() {
        // Arrange
        String signalId = "sig-123";
        LLead lead = new LLead();
        lead.setSourceSignalId(signalId);
        lead.setStatus("NEW");

        when(leadRepository.findBySourceSignalId(signalId)).thenReturn(Optional.of(lead));

        String longReply = "This is a very long reply that is more than fifty characters long, indeed.";

        // Act
        service.executeReply(signalId, longReply);

        // Assert
        verify(leadRepository, times(1)).save(lead);
        assertThat(lead.getTimeline().get(0).getDescription())
                .isEqualTo("AI-Generated reply sent: This is a very long reply that is more than fif...");
    }
}
