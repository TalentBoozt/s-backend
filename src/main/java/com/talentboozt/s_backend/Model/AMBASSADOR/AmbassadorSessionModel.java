package com.talentboozt.s_backend.Model.AMBASSADOR;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter

@Document(collection = "ambassador_sessions")
public class AmbassadorSessionModel {
    @Id
    private String id;

    @Indexed
    private String ambassadorId;

    private String type; // HOSTED, TRAINING
    private String topic;
    private String sessionLink; // Zoom/Meet/Platform URL
    private Instant date;
    private int attendeeCount;

    private boolean completed;
}
