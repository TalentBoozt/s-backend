package com.talentboozt.s_backend.Model.EndUser;

import com.talentboozt.s_backend.DTO.EndUser.EmpFollowersDTO;
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

@Document(collection = "portal_emp_followers")
public class EmpFollowersModel {
    @Id
    private String id;
    private String employeeId;
    @Field("followers")
    private List<EmpFollowersDTO> followers;
}
