package com.sean.ninnong.comment.controller;

import com.sean.ninnong.auth.security.UserPrincipal;
import com.sean.ninnong.comment.dto.CommentRequest;
import com.sean.ninnong.comment.dto.CommentThreadResponse;
import com.sean.ninnong.comment.dto.CommentResponseMsg;
import com.sean.ninnong.comment.service.CommentQueryService;
import com.sean.ninnong.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentQueryService commentQueryService;
    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<CommentResponseMsg> createComment(@PathVariable Long postId, @RequestBody CommentRequest request, @AuthenticationPrincipal UserPrincipal user) {
        commentService.createComment(request, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponseMsg.addComment(postId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentThreadResponse>> findComments(@PathVariable Long postId) {
        return ResponseEntity.ok().body(commentQueryService.findCommentThreads(postId));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponseMsg> modifyComment(@PathVariable Long commentId, @RequestParam String newContent, @AuthenticationPrincipal UserPrincipal user) {
        commentService.modifyComment(commentId, newContent, user.getId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(CommentResponseMsg.modifyComment(commentId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponseMsg> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal UserPrincipal user) {
        commentService.deleteComment(commentId, user.getId());

        return ResponseEntity.ok(CommentResponseMsg.deleteComment(commentId));
    }
}
