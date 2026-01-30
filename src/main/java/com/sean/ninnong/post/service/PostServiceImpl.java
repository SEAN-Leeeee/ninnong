package com.sean.ninnong.post.service;


import com.sean.ninnong.exception.PostNotFoundException;
import com.sean.ninnong.post.dto.PostSummaryResponse;
import com.sean.ninnong.post.domain.Post;
import com.sean.ninnong.post.dto.PostRequest;
import com.sean.ninnong.post.dto.PostResponse;
import com.sean.ninnong.post.repository.PostRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private final PostImageService postImageService;
    private final PostRepository postRepository;

    public PostServiceImpl(PostImageService postImageService, PostRepository postRepository) {
        this.postImageService = postImageService;
        this.postRepository = postRepository;
    }

    private void checkOwnership(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        if (!post.getWriter().equals(currentUserId)) {
            throw new AccessDeniedException("post에 접근권한이 없습니다.");
        }
    }

    @Transactional
    public Long createPost(PostRequest request, Long writer) {
        Post post = Post.create(request, writer);
        postRepository.save(post);
        Optional.ofNullable(request.getImageUrls())
                .filter(list -> !list.isEmpty())
                .ifPresent(list -> postImageService.create(request.getImageUrls(), post.getId(), writer));

        return post.getId();

    }

    @Override
    public List<PostSummaryResponse> getPostList() {
        return postRepository.findActivePostSummaries();
    }

    @Override
    public PostResponse getPostFrom(Long id) {
        return postRepository.findActivePostFrom(id);
    }

    @Transactional
    public void modifyPost(PostRequest modifyPost, Long id, Long modifierId) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        checkOwnership(post.getId(), modifierId);
        post.modify(modifyPost);
    }

    @Override
    public void deletePost(Long id, Long modifierId) {
        checkOwnership(id, modifierId);


        //이미지 삭제
        //댓글 삭제
        //게시글 삭제.


    }
}
