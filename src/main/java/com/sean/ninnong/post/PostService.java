package com.sean.ninnong.post;

import java.util.List;

public interface PostService {

    Long create(PostRequest post, Long creatorId);

    List<PostSummaryResponse> getPostList();

    PostResponse getPostFrom(Long id);

    void modify(PostRequest modifyPost, Long id, Long modifierId
    );

}
