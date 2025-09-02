package com.talentboozt.s_backend.domains._private.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BulkStatusUpdateRequest {
    private List<String> userIds; // optional if using filters
    private Boolean active;
    private Boolean disabled;
    private Boolean ambassador;

    // Optional filters (if applying to all filtered users)
    private String search;
    private String role;
    private String platform;
    private Boolean onlyFilteredUsers;
    private Boolean filterActive;
}
