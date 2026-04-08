package com.talentboozt.s_backend.domains.leads.crm.service;

import com.talentboozt.s_backend.domains.leads.crm.model.LLead;
import com.talentboozt.s_backend.domains.leads.crm.repository.LLeadRepository;
import com.talentboozt.s_backend.domains.leads.model.LRawSignal;
import com.talentboozt.s_backend.domains.leads.repository.LRawSignalRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class LLeadService {

    private final LLeadRepository leadRepository;
    private final LRawSignalRepository rawSignalRepository;

    public LLeadService(LLeadRepository leadRepository, LRawSignalRepository rawSignalRepository) {
        this.leadRepository = leadRepository;
        this.rawSignalRepository = rawSignalRepository;
    }

    public List<LLead> getLeads(String workspaceId, String status, Double minScore) {
        if (status == null && minScore == null) {
            return leadRepository.findByWorkspaceId(workspaceId);
        }
        return leadRepository.findByFilters(workspaceId, status, minScore);
    }

    public LLead getLeadById(String id, String workspaceId) {
        LLead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        
        if (!lead.getWorkspaceId().equals(workspaceId)) {
            throw new RuntimeException("Unauthorized");
        }
        return lead;
    }

    public LLead updateLead(String id, String workspaceId, String status, String note) {
        LLead lead = getLeadById(id, workspaceId);

        boolean updated = false;
        if (status != null && !status.equals(lead.getStatus())) {
            lead.addTimelineEvent("Status Changed", "Status changed from " + lead.getStatus() + " to " + status);
            lead.setStatus(status);
            updated = true;
        }

        if (note != null && !note.isEmpty()) {
            lead.addTimelineEvent("Note Added", note);
            String existingNotes = lead.getNotes() == null ? "" : lead.getNotes() + "\n";
            lead.setNotes(existingNotes + "[" + Instant.now() + "] " + note);
            updated = true;
        }

        if (updated) {
            lead.setUpdatedAt(Instant.now());
            return leadRepository.save(lead);
        }

        return lead;
    }

    public LLead convertFromSignal(String signalId, String workspaceId) {
        LRawSignal signal = rawSignalRepository.findById(signalId)
                .orElseThrow(() -> new RuntimeException("Signal not found"));

        if (!signal.getWorkspaceId().equals(workspaceId)) {
            throw new RuntimeException("Unauthorized");
        }

        LLead lead = new LLead();
        lead.setWorkspaceId(workspaceId);
        lead.setName(signal.getAuthor());
        
        String platform = "UNKNOWN";
        if (signal.getMetadata() != null && signal.getMetadata().get("platform") != null) {
            platform = signal.getMetadata().get("platform").toString();
        }
        lead.setPlatform(platform);
        lead.setScore(signal.getScore());
        lead.setTags(signal.getTags());
        lead.setSourceSignalId(signal.getId());
        
        lead.addTimelineEvent("Lead Created", "Manually converted from raw signal");
        if (signal.getScore() != null) {
            lead.addTimelineEvent("AI Scored", "AI assigned score: " + signal.getScore());
        }

        return leadRepository.save(lead);
    }

    public LLead autoConvert(LRawSignal signal) {
        LLead lead = new LLead();
        lead.setWorkspaceId(signal.getWorkspaceId());
        lead.setName(signal.getAuthor());
        
        String platform = "UNKNOWN";
        if (signal.getMetadata() != null && signal.getMetadata().get("platform") != null) {
            platform = signal.getMetadata().get("platform").toString();
        }
        lead.setPlatform(platform);
        lead.setScore(signal.getScore());
        lead.setTags(signal.getTags());
        lead.setSourceSignalId(signal.getId());
        
        lead.addTimelineEvent("Lead Created", "Automatically promoted by AI Intelligence");
        if (signal.getScore() != null) {
            lead.addTimelineEvent("AI Scored", "AI assigned score: " + signal.getScore() + " (" + signal.getIntent() + ")");
        }

        return leadRepository.save(lead);
    }

    public void deleteLead(String id, String workspaceId) {
        LLead lead = getLeadById(id, workspaceId);
        leadRepository.delete(lead);
    }

    public LLead createLead(LLead lead, String workspaceId) {
        lead.setWorkspaceId(workspaceId);
        lead.setCreatedAt(Instant.now());
        lead.setUpdatedAt(Instant.now());
        lead.addTimelineEvent("Lead Created", "Manually created lead");
        return leadRepository.save(lead);
    }
}
