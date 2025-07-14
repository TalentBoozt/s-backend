package com.talentboozt.s_backend.domains.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class EmpFollowingDTO {
    @Id
    private String id;
    private String followingId;
    private String followingName;
    private String followingOccupation;
    private String followingImage;
    private String followingLocation;
}
