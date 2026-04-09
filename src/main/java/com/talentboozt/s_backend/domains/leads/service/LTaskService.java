package com.talentboozt.s_backend.domains.leads.service;

import com.talentboozt.s_backend.domains.leads.model.LTask;
import com.talentboozt.s_backend.domains.leads.repository.LTaskRepository;
import com.talentboozt.s_backend.domains.leads.model.LNotification;
import com.talentboozt.s_backend.domains.leads.repository.LNotificationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class LTaskService {
    private final LTaskRepository repository;
    private final LNotificationRepository notificationRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    public LTaskService(LTaskRepository repository, 
                        LNotificationRepository notificationRepository,
                        org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public LTask createAndQueueTask(String wsId, String userId, String type, Map<String, Object> metadata) {
        LTask task = new LTask();
        task.setWorkspaceId(wsId);
        task.setUserId(userId);
        task.setType(type);
        task.setMetadata(metadata);
        return repository.save(task);
    }

    @Async
    public void executeAiTemplateGeneration(LTask task) {
        String topic = "/topic/tasks/" + task.getWorkspaceId();
        try {
            task.setStatus("PROCESSING");
            task.setProgress(10);
            repository.save(task);
            messagingTemplate.convertAndSend(topic, task);
            
            // Simulation of AI work
            Thread.sleep(2000);
            task.setProgress(40);
            repository.save(task);
            messagingTemplate.convertAndSend(topic, task);
            
            Thread.sleep(3000);
            task.setProgress(80);
            repository.save(task);
            messagingTemplate.convertAndSend(topic, task);
            
            // Task complete
            task.setStatus("COMPLETED");
            task.setProgress(100);
            task.setResultUrl("/v1/leads/templates/generated-id");
            repository.save(task);
            messagingTemplate.convertAndSend(topic, task);
            
            // Notify user
            LNotification n = new LNotification();
            n.setWorkspaceId(task.getWorkspaceId());
            n.setUserId(task.getUserId());
            n.setTitle("AI Template Strategy Ready");
            n.setMessage("Your personalized outreach strategy is ready for review.");
            n.setType("system");
            notificationRepository.save(n);
            messagingTemplate.convertAndSend("/topic/notifications/" + n.getWorkspaceId(), n);
            
        } catch (Exception e) {
            task.setStatus("FAILED");
            task.setErrorMessage(e.getMessage());
            repository.save(task);
            messagingTemplate.convertAndSend(topic, task);
        }
    }
}
