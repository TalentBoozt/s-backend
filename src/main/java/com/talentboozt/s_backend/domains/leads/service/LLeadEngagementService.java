package com.talentboozt.s_backend.domains.leads.service;

import com.talentboozt.s_backend.domains.leads.crm.model.LLead;
import com.talentboozt.s_backend.domains.leads.crm.repository.LLeadRepository;
import com.talentboozt.s_backend.domains.leads.model.LRawSignal;
import com.talentboozt.s_backend.domains.leads.repository.LRawSignalRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class LLeadEngagementService {

    private final LAIService aiService;
    private final LLeadRepository leadRepository;
    private final LRawSignalRepository rawSignalRepository;

    public LLeadEngagementService(LAIService aiService, LLeadRepository leadRepository, LRawSignalRepository rawSignalRepository) {
        this.aiService = aiService;
        this.leadRepository = leadRepository;
        this.rawSignalRepository = rawSignalRepository;
    }

    public String generateDraft(String signalId, String tone) {
        Optional<LRawSignal> signal = rawSignalRepository.findById(signalId);
        if (signal.isPresent()) {
            return aiService.generateReply(signal.get().getContent(), tone);
        }
        return "Sorry, I couldn't find the original signal to generate a reply.";
    }

    public void executeReply(String signalId, String replyText) {
        // Here we would actually call the platform API (Reddit, etc.)
        // For MVP, we simulate success and log it.
        
        // Find if this signal is already a lead, if not, it should probably be converted or just logged.
        Optional<LLead> leadOpt = leadRepository.findBySourceSignalId(signalId);
        
        if (leadOpt.isPresent()) {
            LLead lead = leadOpt.get();
            lead.setStatus("CONTACTED");
            lead.addTimelineEvent("REPLY_SENT", "AI-Generated reply sent: " + truncate(replyText));
            lead.setUpdatedAt(Instant.now());
            leadRepository.save(lead);
        } else {
            // Log for raw signal if no lead exists
            Optional<LRawSignal> signalOpt = rawSignalRepository.findById(signalId);
            signalOpt.ifPresent(signal -> {
                signal.setStatus("CONTACTED");
                rawSignalRepository.save(signal);
                // In a real scenario, we might want to auto-create a lead here too.
            });
        }
    }

    private String truncate(String text) {
        if (text == null) return "";
        return text.length() > 50 ? text.substring(0, 47) + "..." : text;
    }
}
