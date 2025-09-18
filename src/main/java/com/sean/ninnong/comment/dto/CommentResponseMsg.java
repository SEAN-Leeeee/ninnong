package com.sean.ninnong.comment.dto;

import java.time.LocalDateTime;

public record CommentResponseMsg(Long id, String message, LocalDateTime processedAt) {

    public static CommentResponseMsg addComment(Long postId) {
        return new CommentResponseMsg(
                postId,
                "번 글에 댓글이 추가되었습니다.",
                LocalDateTime.now()
        );
    }

    public static CommentResponseMsg modifyComment(Long commentId) {
        return new CommentResponseMsg(
                commentId,
                "번 댓글이 수정되었습니다.",
                LocalDateTime.now()
        );
    }

    public static CommentResponseMsg deleteComment(Long commentId) {
        return new CommentResponseMsg(
                commentId,
                "번 댓글이 삭제되었습니다.",
                LocalDateTime.now()
        );
    }
}
