package com.talentboozt.s_backend.DTO.COM_COURSES;

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
