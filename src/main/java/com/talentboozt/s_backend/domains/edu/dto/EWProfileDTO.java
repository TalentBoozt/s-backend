package com.talentboozt.s_backend.domains.edu.dto;

import com.talentboozt.s_backend.domains.common.dto.SocialLinksDTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EWProfileDTO {
    private String name;
    private String description;
    private String status;
    private SocialLinksDTO socialLinks;
    
    // Arrays for channels and threads should be stored in their own MongoDB collections 
    // to avoid unbounded array growth inside EWorkspaces.
}
