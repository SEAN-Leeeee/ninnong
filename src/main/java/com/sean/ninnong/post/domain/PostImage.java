package com.sean.ninnong.post.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String url;
    @Column(nullable = false)
    private Long postId;
    @Column(nullable = false)
    private Long writer;

    public PostImage(String url, Long postId, Long writer) {
        this.url = url;
        this.postId = postId;
        this.writer = writer;
    }

    public static PostImage of(String url, Long postId, Long writer) {
        return new PostImage(url, postId, writer);
    }
}
