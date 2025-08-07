package com.talentboozt.s_backend.domains.common.model;

import com.talentboozt.s_backend.domains.common.dto.LoginEventDTO;
import com.talentboozt.s_backend.domains.common.dto.LoginMetaDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

@Document(collection = "portal_logins")
@CompoundIndex(def = "{'userId': 1}")
public class Login {
    @Id
    private String id;
    private String userId;
    private List<String> loginDates; //deprecated
    private List<LoginMetaDTO> metaData; //deprecated
    private List<LoginEventDTO> events = new ArrayList<>();
}
