package com.sean.ninnong.comment.service;

import com.sean.ninnong.comment.domain.Comment;
import com.sean.ninnong.comment.dto.ChildCommentResponse;
import com.sean.ninnong.comment.dto.CommentThreadResponse;
import com.sean.ninnong.comment.repository.CommentRepository;
import com.sean.ninnong.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryServiceImpl implements CommentQueryService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    @Override
    public List<CommentThreadResponse> findCommentThreads(Long postId) {
        List<Comment> allComments = commentRepository.findByPostIdOrderByCreatedAtAsc(postId);

        List<Comment> roots = new ArrayList<>();
        Map<Long, List<ChildCommentResponse>> childrenByRootId = new HashMap<>();

        for (Comment c : allComments) {
            if (c.getParentId() == null) {
                roots.add(c);
                childrenByRootId.putIfAbsent(c.getId(), new ArrayList<>());
            }
        }

        for (Comment c : allComments) {
            Long parentId = c.getParentId();
            if(parentId == null) continue;
            String nickname = userService.getNickname(c.getWriter());
            childrenByRootId.get(parentId).add(ChildCommentResponse.of(c, nickname));
        }

        return roots.stream()
                .map(root -> CommentThreadResponse.of(
                        root,
                        userService.getNickname(root.getWriter()),
                        childrenByRootId.getOrDefault(root.getId(), List.of())
                ))
                .toList();
    }

}
