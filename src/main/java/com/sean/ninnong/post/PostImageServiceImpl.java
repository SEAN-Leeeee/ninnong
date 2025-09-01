package com.sean.ninnong.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostImageServiceImpl implements PostImageService{

    private final PostImageRepository postImageRepository;

    public PostImageServiceImpl(PostImageRepository postImageRepository) {
        this.postImageRepository = postImageRepository;
    }

    @Override
    @Transactional
    public void create(List<String> imageList, Long postId, Long creatorId) {
        for (String url : imageList) {
            postImageRepository.save(PostImage.of(url, postId, creatorId));
        }
    }
}
