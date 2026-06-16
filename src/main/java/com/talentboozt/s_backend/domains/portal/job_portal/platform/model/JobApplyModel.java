package com.talentboozt.s_backend.domains.portal.job_portal.platform.model;

import com.talentboozt.s_backend.domains.portal.job_portal.platform.dto.JobApplicantDTO;
import com.talentboozt.s_backend.domains.portal.job_portal.platform.dto.JobViewerDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "portal_job_applicants")
public class JobApplyModel {
    @Id
    private String id;
    private String companyId;
    private String jobId;
    @Field("applicants")
    List<JobApplicantDTO> applicants;
    @Field("viewers")
    List<JobViewerDTO> viewers;
}
