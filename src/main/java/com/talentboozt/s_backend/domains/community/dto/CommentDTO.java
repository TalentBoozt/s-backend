package com.talentboozt.s_backend.domains.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private String id;
    private String postId;
    private String parentId;
    private String authorId;
    private String text;
    private int upvotes;
    private int downvotes;
    private List<PostDTO.ReactionDTO> reactions;
    private List<String> mentionIds;
    private String timestamp;
    private List<CommentDTO> replies;
}
