package com.talentboozt.s_backend.domains.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class EmpSkillsDTO {
    @Id
    private String id;
    private String Skill;
    private String percentage;
}
