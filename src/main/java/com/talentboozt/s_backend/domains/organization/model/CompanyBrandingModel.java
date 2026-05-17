package com.talentboozt.s_backend.domains.organization.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "company_branding")
public class CompanyBrandingModel {
    @Id
    private String id;
    private String organizationId;
    
    private String primaryColor;
    private String secondaryColor;
    private String bannerUrl;
    
    private List<String> galleryUrls;
    private List<String> values;
    private String cultureDescription;
    
    private SocialLinks socialLinks;

    @Data
    public static class SocialLinks {
        private String linkedin;
        private String twitter;
        private String github;
        private String glassdoor;
    }
}
