package com.talentboozt.s_backend.domains.edu.dto;

import com.talentboozt.s_backend.domains.edu.enums.ENotificationType;

import lombok.Data;

@Data
public class ENotificationDTO {
    private String id;
    private String userId;
    private String workspaceId;
    private ENotificationType type;
    private String title = "Notification";
    private String message = "You have a new notification";
    private String url = "";
    private String icon = "bell";
    private String color = "#000000";
    private String backgroundColor = "#000000";
    private String textColor = "#ffffff";
    private String borderColor = "#000000";
    private Boolean Read = false;
    private Boolean Archived = false;
    private Boolean Deleted = false;
    private Boolean Pinned = false;
    private Boolean Important = false;
}
