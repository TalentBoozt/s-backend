package com.talentboozt.s_backend.domains.leads.events;

import com.talentboozt.s_backend.domains.leads.model.LRawSignal;
import org.springframework.context.ApplicationEvent;

public class LNewSignalEvent extends ApplicationEvent {
    private final LRawSignal rawSignal;

    public LNewSignalEvent(Object source, LRawSignal rawSignal) {
        super(source);
        this.rawSignal = rawSignal;
    }

    public LRawSignal getRawSignal() {
        return rawSignal;
    }
}
