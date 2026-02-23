package com.sean.ninnong.post.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String url;
    @Column(nullable = false)
    @ManyToOne
    private Post post;
    @Column(nullable = false)
    private Long writer;

    public PostImage(String url, Post post, Long writer) {
        this.url = url;
        this.post = post;
        this.writer = writer;
    }

    public static PostImage of(String url, Post post, Long writer) {
        return new PostImage(url, post, writer);
    }
}
