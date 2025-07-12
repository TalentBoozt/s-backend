package com.talentboozt.s_backend.domains.common.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter

@Document(collection = "portal_systemNotifications")
public class SystemNotificationsModel {
    @Id
    private String id;
    private String message;
    private Date startTime;
    private Date endTime;
    private String url;
    private boolean active;
}
