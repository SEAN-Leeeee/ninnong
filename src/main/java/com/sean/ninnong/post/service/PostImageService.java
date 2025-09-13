package com.sean.ninnong.post.service;

import java.util.List;

public interface PostImageService {

    void create(List<String> imageList, Long postId, Long creatorId);
}
