package com.sean.ninnong.comment.domain;

import com.sean.ninnong.common.BaseEntity;
import com.sean.ninnong.post.domain.Post;
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

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    private Comment parent;

    @Column(nullable = false)
    private boolean isDeleted = false;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    public Comment(String content, Long writer, Post post, Comment parent) {
        this.content = content;
        this.writer = writer;
        this.post = post;
        this.parent = parent;
        this.isDeleted = false;
    }

    public void modifyContent(String newComment) {
        this.content = newComment;
    }

    public void deleteComment(Long id) {
        this.isDeleted = true;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}
