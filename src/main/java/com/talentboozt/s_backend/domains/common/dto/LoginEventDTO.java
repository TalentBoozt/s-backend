package com.talentboozt.s_backend.domains.common.dto;

import com.talentboozt.s_backend.domains.common.dto.LoginMetaDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginEventDTO {
    private String date; // e.g., 2025-08-07
    private boolean login;
    private int taskCompletions;
    private int referrals;
    private int redeems;
    private int courseParticipation;
    private int courseConduct;
    private List<LoginMetaDTO> metadata; // Meta info collected during login
}

