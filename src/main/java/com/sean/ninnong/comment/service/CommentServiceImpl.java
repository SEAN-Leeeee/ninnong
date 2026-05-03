package com.sean.ninnong.comment.service;

import com.sean.ninnong.comment.domain.Comment;
import com.sean.ninnong.comment.dto.CommentRequest;
import com.sean.ninnong.comment.repository.CommentRepository;
import com.sean.ninnong.post.domain.Post;
import com.sean.ninnong.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    private static void authChecking(Long userId, Comment comment) {
        if (!comment.getWriter().equals(userId)) {
            throw new SecurityException("작성자만 수정할 수 있습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("comment not found:" + commentId));
    }

    private Comment getCommentAndAuthCheck(Long commentId, Long userId) {
        Comment comment = getComment(commentId);
        authChecking(userId, comment);
        return comment;
    }

    @Override
    @Transactional
    public Long createComment(CommentRequest request, Long postId, Long writer) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("post not found: " + postId));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("parent comment not found: " + request.getParentId()));
        }

        Comment comment = new Comment(request.getContent(), writer, post, parent);
        Comment savedComment = commentRepository.save(comment);
        return savedComment.getId();
    }

    @Override
    @Transactional
    public void modifyComment(Long commentId, String newContent, Long userId) {
        Comment comment = getCommentAndAuthCheck(commentId, userId);
        comment.modifyContent(newContent);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getCommentAndAuthCheck(commentId, userId);
        comment.deleteComment(comment.getId());
    }

}
