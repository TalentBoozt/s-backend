package com.talentboozt.s_backend.domains.portal.courses.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseMissedNotify {
    String email;
    String lastMissedBatch;
    String date;
}
