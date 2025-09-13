package com.sean.ninnong.post.controller;

import com.sean.ninnong.auth.security.UserPrincipal;
import com.sean.ninnong.post.dto.PostRequest;
import com.sean.ninnong.post.dto.PostResponse;
import com.sean.ninnong.post.dto.PostResponseMsg;
import com.sean.ninnong.post.dto.PostSummaryResponse;
import com.sean.ninnong.post.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostSummaryResponse>> getPostList() {
        return ResponseEntity.ok().body(postService.getPostList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok().body(postService.getPostFrom(id));
    }
    @PostMapping
    public ResponseEntity<PostResponseMsg> createPost(@RequestBody PostRequest post, @AuthenticationPrincipal UserPrincipal user) {
        Long id = postService.create(post, user.getId());

        return ResponseEntity.ok().body(PostResponseMsg.create(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostResponseMsg> modifyPost(@PathVariable Long id, @RequestBody PostRequest modifyPost, @AuthenticationPrincipal UserPrincipal user) {
        postService.modify(modifyPost, id, user.getId());

        return ResponseEntity.ok().body(PostResponseMsg.modify(id));
    }

}
