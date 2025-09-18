package com.sean.ninnong.comment.service;

import com.sean.ninnong.comment.dto.CommentThreadResponse;

import java.util.List;

public interface CommentQueryService {

    List<CommentThreadResponse> findCommentThreads(Long postId);
}
