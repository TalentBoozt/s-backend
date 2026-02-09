package com.talentboozt.s_backend.domains.community.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;
    private String postId;
    private String parentId; // For threaded comments
    private String authorId;
    private List<String> mentionIds;
    private String text;
    private int upvotes;
    private int downvotes;
    private List<Post.Reaction> reactions;
    private LocalDateTime timestamp;
    private List<Comment> replies;
}
