package com.talentboozt.s_backend.Model.PLAT_JOB_PORTAL;

import com.talentboozt.s_backend.DTO.PLAT_JOB_PORTAL.JobApplicantDTO;
import com.talentboozt.s_backend.DTO.PLAT_JOB_PORTAL.JobViewerDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@ToString

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
