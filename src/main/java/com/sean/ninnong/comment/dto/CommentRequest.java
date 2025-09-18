package com.sean.ninnong.comment.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentRequest {

    private final String content;
    private final Long postId;
    private final Long parentId;
    private final boolean isDeleted;

    private CommentRequest(String content, Long postId, Long parentId, boolean isDeleted) {
        this.content = content;
        this.postId = postId;
        this.parentId = parentId;
        this.isDeleted = isDeleted;
    }

}
