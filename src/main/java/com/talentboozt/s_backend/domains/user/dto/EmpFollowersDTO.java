package com.talentboozt.s_backend.domains.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpFollowersDTO {
    @Id
    private String id;
    private String followerId;
    private String followerName;
    private String followerOccupation;
    private String followerImage;
    private String followerLocation;
}
