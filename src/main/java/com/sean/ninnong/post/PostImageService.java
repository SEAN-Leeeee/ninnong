package com.sean.ninnong.post;

import java.util.List;

public interface PostImageService {

    void create(List<String> imageList, Long postId, Long creatorId);
}
