package com.talentboozt.s_backend.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class EmpFollowersDTO {
    @Id
    private String id;
    private String followerId;
    private String followerName;
    private String followerOccupation;
    private String followerImage;
    private String followerLocation;
}
