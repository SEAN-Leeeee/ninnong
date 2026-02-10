package com.sean.ninnong.comment.domain;

import com.sean.ninnong.comment.dto.CommentRequest;
import com.sean.ninnong.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long writer;

    @Column(nullable = false)
    private Long postId;

    private Long parentId;

    @Column(nullable = false)
    private Boolean isDeleted = false;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public static Comment create(CommentRequest request, Long writer) {
        return new Comment(request.getContent(), writer, request.getPostId(), request.getParentId());
    }

    public Comment(String content, Long writer, Long postId, Long parentId) {
        this.content = content;
        this.writer = writer;
        this.postId = postId;
        this.parentId = parentId;
        this.isDeleted = false;
    }

    public void modifyContent(String newComment) {
        this.content = newComment;
    }

    public void deleteComment(Long id) {
        this.isDeleted = true;
    }
}
