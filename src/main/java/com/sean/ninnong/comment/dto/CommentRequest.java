package com.sean.ninnong.comment.dto;

import com.sean.ninnong.comment.domain.Comment;
import com.sean.ninnong.post.domain.Post;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommentRequest {

    private final String content;
    private final Post post;
    private final Comment parent;
    private final boolean isDeleted;

    private CommentRequest(String content, Post post, Comment parent, boolean isDeleted) {
        this.content = content;
        this.post = post;
        this.parent = parent;
        this.isDeleted = isDeleted;
    }

}
