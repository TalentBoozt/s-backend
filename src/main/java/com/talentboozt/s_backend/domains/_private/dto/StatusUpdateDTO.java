package com.talentboozt.s_backend.domains._private.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateDTO {
    private boolean active;
    private boolean disabled;
    private boolean ambassador;
}
