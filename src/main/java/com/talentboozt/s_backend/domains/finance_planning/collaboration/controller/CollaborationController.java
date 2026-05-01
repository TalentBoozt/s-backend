package com.talentboozt.s_backend.domains.finance_planning.collaboration.controller;

import com.talentboozt.s_backend.domains.finance_planning.collaboration.models.CollaborationOperation;
import com.talentboozt.s_backend.domains.finance_planning.collaboration.models.PresenceUpdate;
import com.talentboozt.s_backend.domains.finance_planning.collaboration.presence.PresenceManager;
import com.talentboozt.s_backend.domains.finance_planning.collaboration.service.OperationProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CollaborationController {
    private final OperationProcessor operationProcessor;
    private final PresenceManager presenceManager;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/project/{projectId}/operation")
    public void handleOperation(@DestinationVariable String projectId, @Payload CollaborationOperation operation) {
        log.info("Received operation for project {}: {}", projectId, operation);
        operation.setProjectId(projectId);
        operationProcessor.processOperation(operation);
    }

    @MessageMapping("/project/{projectId}/presence")
    public void handlePresence(@DestinationVariable String projectId, @Payload PresenceUpdate presence) {
        presence.setProjectId(projectId);
        presenceManager.updatePresence(presence);
        
        // Broadcast active users to the project channel
        List<PresenceUpdate> activeUsers = presenceManager.getActiveUsers(projectId);
        messagingTemplate.convertAndSend("/topic/project/" + projectId + "/presence_update", activeUsers);
    }
    
    @MessageMapping("/project/{projectId}/connect")
    public void handleConnect(@DestinationVariable String projectId) {
        log.info("User connecting to project: {}", projectId);
        // Could send initial state or current active users
        List<PresenceUpdate> activeUsers = presenceManager.getActiveUsers(projectId);
        messagingTemplate.convertAndSend("/topic/project/" + projectId + "/presence_update", activeUsers);
    }
}
