package com.sean.ninnong.post;


import com.sean.ninnong.exception.PostNotFoundException;
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

    @Override
    @Transactional
    public Long create(PostRequest request, Long creatorId) {
        //검사
        Post post = Post.create(request, creatorId);
        postRepository.save(post);
        Optional.ofNullable(request.getImageUrls())
                .filter(list -> !list.isEmpty())
                .ifPresent(list -> postImageService.create(request.getImageUrls(), post.getId(), creatorId));

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

    @Override
    @Transactional
    public void modify(PostRequest modifyPost, Long id, Long modifierId) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        checkOwnership(post.getId(), modifierId);
        post.modify(modifyPost);
    }

}
