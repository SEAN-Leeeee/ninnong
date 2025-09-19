package com.sean.ninnong.comment.dto;

import com.sean.ninnong.comment.domain.Comment;

import java.time.LocalDateTime;

public record ChildCommentResponse(
        Long id,
        Long parentId,
        Long writer,
        String writerNickname,
        String content,
        boolean idDeleted,
        LocalDateTime createdAt
) {
    public static ChildCommentResponse of(Comment parent,String writerNickname) {
        return new ChildCommentResponse(
                parent.getId(),
                parent.getParentId(),
                parent.getWriter(),
                writerNickname,
                parent.isDeleted() ? "삭제된 댓글입니다" : parent.getContent(),
                parent.isDeleted(),
                parent.getCreatedAt()
        );
    }
}
