package com.sean.ninnong.post.service;

public class CommentDeletionStep implements PostDeletionStep {

    @Override
    public void delete(Long postId, Long actorId) {

    }

    @Override
    public int order() {
        return PostDeletionStep.super.order();
    }
}

