package com.sean.ninnong.post.domain;

import com.sean.ninnong.common.enums.Category;
import com.sean.ninnong.common.enums.PostSubject;
import com.sean.ninnong.post.dto.PostRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    private String title;

    @Column(length = 5000, nullable = false)
    @Size(max = 5000, message = "내용은 최대 5000자까지 입력 가능합니다.")
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
    @Enumerated(EnumType.STRING)
    private PostSubject subject;
    @Column(nullable = false)
    private Long writer;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;

    private Post(PostRequest post, Long creatorId) {
        changeTitle(post.getTitle());
        changeContent(post.getContent());
        changeCategory(post.getCategory());
        changeSubject(post.getSubject());
        this.writer = creatorId;
        touchCreatedAt();
        this.modifiedAt = null;
        this.deletedAt = null;
    }

    public static Post create(PostRequest post, Long writer) {
        return new Post(post, writer);
    }

    public Long getId() {
        return id;
    }

    public Long getWriter() {
        return writer;
    }

    public void modify(PostRequest modifyPost) {
        if (modifyPost.getTitle() != null) {
            changeTitle(modifyPost.getTitle());
        }

        if (modifyPost.getContent() != null) {
            changeContent(modifyPost.getContent());
        }

        if (modifyPost.getCategory() != null) {
            changeCategory(modifyPost.getCategory());
        }

        changeSubject(modifyPost.getSubject());

        touchModifiedAt();

    }

    private void changeSubject(PostSubject subject) {

        System.out.println("@@ 서브젝트 : " +subject );
        System.out.println("@@ 서브젝트 네임: " + subject.name() );
        this.subject = subject;
    }

    private void changeCategory(Category category) {
        validateCategory(category);
        this.category = category;
    }

    private void validateCategory(Category category) {
        System.out.println("@@ 카테고리 : " + category );
        System.out.println("@@ 카테고리 네임: " + category.name() );
        if(category == null) throw new IllegalArgumentException("카테고리는 비어있을 수 없습니다.");
    }

    private void changeContent(String content) {
        validateContent(content);
        this.content = content;
    }

    private void validateContent(String content) {
        if(content.isBlank()) throw new IllegalArgumentException("본문의 내용은 비어있을 수 없습니다.");
        if(content.length() > 50000) throw new IllegalArgumentException("본문의 허용 길이를 초과했습니다.");
    }

    private void changeTitle(String newTitle) {
        validateTitle(newTitle);
        this.title = newTitle;
    }

    private void validateTitle(String title) {
        if(title.isBlank()) throw new IllegalArgumentException("제목은 비어있을 수 없습니다.");
        if(title.length() > 255) throw new IllegalArgumentException("제목은 255자를 넘지 않아야합니다.");
    }

    private void touchModifiedAt() {
        this.modifiedAt = LocalDateTime.now();
    }

    private void touchCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }
}
