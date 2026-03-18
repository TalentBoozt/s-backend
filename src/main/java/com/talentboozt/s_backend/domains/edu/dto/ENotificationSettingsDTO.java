package com.talentboozt.s_backend.domains.edu.dto;

import lombok.Data;

@Data
public class ENotificationSettingsDTO {
    private boolean emailNotifications = true;
    private boolean pushNotifications = true;
    private boolean smsNotifications = true;
    private boolean inAppNotifications = true;
    private boolean emailMarketing = true;
    private boolean smsMarketing = true;
    private boolean inAppMarketing = true;
    private boolean emailPromotional = true;
    private boolean smsPromotional = true;
    private boolean inAppPromotional = true;
}
