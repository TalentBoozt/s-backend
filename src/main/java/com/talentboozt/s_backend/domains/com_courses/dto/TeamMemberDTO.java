package com.talentboozt.s_backend.domains.com_courses.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeamMemberDTO {
    private String userId;
    private String role; // Admin, Recruiter, Manager, Trainer
    private List<String> permissions; // ["CAN_POST_JOBS", "CAN_MANAGE_USERS"]
}
