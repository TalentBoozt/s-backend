package com.talentboozt.s_backend.domains.edu.model;

import com.talentboozt.s_backend.domains.edu.enums.EGradingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_assignment_submissions")
@CompoundIndexes({
    @CompoundIndex(name = "user_assignment_idx", def = "{'userId': 1, 'assignmentId': 1}")
})
public class EAssignmentSubmissions {
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    @Indexed
    private String assignmentId;
    
    private String content; // text content submitted by user
    private String[] attachmentUrls; // files uploaded
    
    private EGradingStatus status;
    private Double score;
    private String feedback;
    
    private String gradedBy;
    private Instant gradedAt;
    
    @CreatedDate
    private Instant submittedAt;
}
