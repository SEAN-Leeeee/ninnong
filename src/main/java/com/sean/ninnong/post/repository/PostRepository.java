package com.sean.ninnong.post.repository;

import com.sean.ninnong.post.dto.PostResponse;
import com.sean.ninnong.post.dto.PostSummaryResponse;
import com.sean.ninnong.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
    SELECT new com.sean.ninnong.post.dto.PostSummaryResponse(
        p.id, p.title, p.category, p.subject, p.writer.id, p.writer.nickname, p.createdAt
    )
    FROM Post p
    WHERE p.deletedAt is null
    ORDER BY p.createdAt DESC
""")
    List<PostSummaryResponse> findActivePostSummaries();

    @Query("""
    SELECT new com.sean.ninnong.post.dto.PostResponse(
        p.id, p.title, p.content, p.category, p.subject, p.writer.id, p.writer.nickname, p.createdAt
    )
    FROM Post p
    WHERE p.id = :id AND p.deletedAt is null
""")
    PostResponse findActivePostFrom(Long id);
}
