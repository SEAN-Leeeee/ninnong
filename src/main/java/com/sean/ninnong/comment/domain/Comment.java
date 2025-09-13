package com.sean.ninnong.comment.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long writer;

    @Column(nullable = false)
    private Long postId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int depth = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private boolean isDeleted = false;

//    // === 비즈니스 메서드 === //
//
//    public void updateContent(String newContent) {
//        this.content = newContent;
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    public void delete() {
//        this.isDeleted = true;
//        this.deletedAt = LocalDateTime.now();
//        this.content = "삭제된 댓글입니다.";
//    }
//
//    public void addChild(Comment child) {
//        child.setParent(this);
//        child.setDepth(this.depth + 1); // 부모 depth + 1
//        this.children.add(child);
//    }
}
