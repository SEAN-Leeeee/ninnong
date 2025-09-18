package com.sean.ninnong.comment.dto;

import com.sean.ninnong.comment.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;

public record CommentThreadResponse(Long id,
                                    Long writer,
                                    String content,
                                    boolean isDeleted,
                                    LocalDateTime createdAt,
                                    List<ChildCommentResponse> children) {

    public static CommentThreadResponse of(Comment root, List<ChildCommentResponse> children) {
        return new CommentThreadResponse(
                root.getId(),
                root.getWriter(),
                root.isDeleted() ? "삭제된 댓글입니다" : root.getContent(),
                root.isDeleted(),
                root.getCreatedAt(),
                children
        );
    }

}
