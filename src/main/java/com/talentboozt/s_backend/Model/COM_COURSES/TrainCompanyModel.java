package com.talentboozt.s_backend.Model.COM_COURSES;

import com.talentboozt.s_backend.DTO.COM_COURSES.TeamMemberDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString

@Document(collection = "train_company")
public class TrainCompanyModel {
    @Id
    private String id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private List<TeamMemberDTO> teamMembers;
}
