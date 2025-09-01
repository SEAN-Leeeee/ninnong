package com.sean.ninnong.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
        SELECT p.id, p.title, p.category, p.subject, p.writer, u.nickname, p.createdAt
        FROM Post p
        JOIN User u ON u.id = p.writer
        WHERE p.deletedAt is null
        ORDER BY p.createdAt DESC
    """)
    List<PostSummaryResponse> findActivePostSummaries();

    @Query(""" 
        select p.id, p.title, p.content, p.category, p.subject, p.writer, u.nickname, p.createdAt
        from Post p
        join User u on u.id = p.writer
        where p.id =:id AND p.deletedAt is null
""")
    PostResponse findActivePostFrom(Long id);
}
