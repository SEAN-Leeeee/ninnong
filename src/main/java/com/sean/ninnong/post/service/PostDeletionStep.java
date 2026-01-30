package com.sean.ninnong.post.service;

public interface PostDeletionStep {

    void delete(Long postId, Long actorId);

    default int order() {
        return 0;
    }
}
