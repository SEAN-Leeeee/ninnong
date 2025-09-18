package com.sean.ninnong.comment.service;

import com.sean.ninnong.comment.domain.Comment;
import com.sean.ninnong.comment.dto.CommentRequest;
import org.springframework.transaction.annotation.Transactional;

public interface CommentService {

    Long createComment(CommentRequest request, Long writer);

    Comment getComment(Long postId);

    @Transactional
    void modifyComment(Long commentId, String newContent, Long userId);

    void deleteComment(Long commentId, Long userId);
}
