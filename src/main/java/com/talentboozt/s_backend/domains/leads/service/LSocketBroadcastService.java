package com.talentboozt.s_backend.domains.leads.service;

import com.talentboozt.s_backend.domains.leads.events.LNewSignalEvent;
import com.talentboozt.s_backend.domains.leads.model.LRawSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LSocketBroadcastService {

    private static final Logger log = LoggerFactory.getLogger(LSocketBroadcastService.class);
    private final SimpMessagingTemplate messagingTemplate;

    public LSocketBroadcastService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Async
    @EventListener
    public void handleNewSignalEvent(LNewSignalEvent event) {
        LRawSignal signal = event.getRawSignal();
        String destination = "/topic/signals/" + signal.getWorkspaceId();
        log.debug("Broadcasting new signal to websocket: {}", destination);
        messagingTemplate.convertAndSend(destination, signal);
    }
}
