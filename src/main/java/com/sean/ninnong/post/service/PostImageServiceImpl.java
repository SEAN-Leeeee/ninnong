package com.sean.ninnong.post.service;

import com.sean.ninnong.post.domain.PostImage;
import com.sean.ninnong.post.repository.PostImageRepository;
import com.sean.ninnong.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sean.ninnong.post.domain.Post;

import java.util.List;

@Service
public class PostImageServiceImpl implements PostImageService{

    private final PostImageRepository postImageRepository;
    private final PostRepository postRepository;

    public PostImageServiceImpl(PostImageRepository postImageRepository, PostRepository postRepository) {
        this.postImageRepository = postImageRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public void create(List<String> imageList, Long postId, Long creatorId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found")); // Or a more specific exception

        for (String url : imageList) {
            postImageRepository.save(PostImage.of(url, post, creatorId));
        }
    }
}
