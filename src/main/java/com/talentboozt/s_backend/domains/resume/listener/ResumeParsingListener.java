package com.talentboozt.s_backend.domains.resume.listener;

import com.talentboozt.s_backend.domains.resume.event.ResumeUploadedEvent;
import com.talentboozt.s_backend.domains.resume.event.ResumeParsedEvent;
import com.talentboozt.s_backend.domains.resume.model.ResumeModel;
import com.talentboozt.s_backend.domains.resume.repository.mongodb.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResumeParsingListener {

    private final ResumeRepository resumeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Async
    @EventListener
    public void handleResumeUploaded(ResumeUploadedEvent event) {
        log.info("Starting async parsing for resume: {}", event.getResumeId());
        
        try {
            // 1. Update status to PARSING
            ResumeModel resume = resumeRepository.findById(event.getResumeId()).orElseThrow();
            resume.setParsingStatus("PARSING");
            resumeRepository.save(resume);

            // 2. Simulate AI Parsing (Placeholder for actual AI service call)
            // In production, this would call an AI service to extract data from the PDF/DOCX
            Thread.sleep(5000); 

            // 3. Mock parsed data
            resume.setParsingStatus("PARSED");
            resume.setUpdatedAt(Instant.now());
            
            // Example of extracted info
            if (resume.getPersonalInfo() == null) {
                resume.setPersonalInfo(new ResumeModel.PersonalInfo());
            }
            // resume.getPersonalInfo().setFullName("Extracted Name"); 
            
            resumeRepository.save(resume);
            log.info("Successfully parsed resume: {}", event.getResumeId());

            // 4. Emit Parsed Event
            eventPublisher.publishEvent(new ResumeParsedEvent(this, resume.getId(), resume.getEmployeeId()));

        } catch (Exception e) {
            log.error("Failed to parse resume: {}", event.getResumeId(), e);
            resumeRepository.findById(event.getResumeId()).ifPresent(resume -> {
                resume.setParsingStatus("FAILED");
                resumeRepository.save(resume);
            });
        }
    }
}
