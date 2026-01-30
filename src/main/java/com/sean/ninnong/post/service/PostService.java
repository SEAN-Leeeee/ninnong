package com.sean.ninnong.post.service;

import com.sean.ninnong.post.dto.PostSummaryResponse;
import com.sean.ninnong.post.dto.PostRequest;
import com.sean.ninnong.post.dto.PostResponse;

import java.util.List;

public interface PostService {

    Long createPost(PostRequest post, Long creatorId);

    List<PostSummaryResponse> getPostList();

    PostResponse getPostFrom(Long id);

    void modifyPost(PostRequest modifyPost, Long id, Long modifierId);

    void deletePost(Long postId, Long modifierId);
}
