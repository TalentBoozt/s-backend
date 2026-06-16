package com.talentboozt.s_backend.domains.portal.content.announcement.event;

import com.talentboozt.s_backend.domains.portal.content.announcement.model.Announcement;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AnnouncementPublishedEvent extends ApplicationEvent {
    private final Announcement announcement;

    public AnnouncementPublishedEvent(Object source, Announcement announcement) {
        super(source);
        this.announcement = announcement;
    }
}
