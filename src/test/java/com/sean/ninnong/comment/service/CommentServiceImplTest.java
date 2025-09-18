package com.sean.ninnong.comment.service;

import com.sean.ninnong.comment.domain.Comment;
import com.sean.ninnong.comment.dto.CommentRequest;
import com.sean.ninnong.common.enums.PostSubject;
import com.sean.ninnong.post.dto.PostRequest;
import com.sean.ninnong.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.sean.ninnong.common.enums.Category.FREE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class CommentServiceImplTest {

    @Autowired
    PostService postService;

    @Autowired
    CommentService commentService;
    @Autowired
    CommentQueryService commentQueryService;



    private Long createComment() {
        CommentRequest request = CommentRequest.builder()
                .content("content1")
                .postId(1L)
                .parentId(null)
                .isDeleted(false)
                .build();
        return commentService.createComment(request, 1L);
    }

    Long createPost() {
        return postService.create(PostRequest.builder()
                .category(FREE)
                .subject(PostSubject.FREE)
                .title("테스트")
                .content("테스트컨텐츠입니다다다닫")
                .imageUrls(null)
                .build(), 1L);
    }

    @DisplayName("루트 코멘트를 작성해서 저장한다")
    @Test
    void saveRootComment() {

        //given
        Long postId = createPost();
        Long commentId = createComment();

        //when
        Comment comment = commentService.getComment(commentId);

        //then
        assertThat(comment.getPostId()).isEqualTo(postId);
        assertThat(comment.getContent()).isEqualTo("content1");
        assertThat(comment.getParentId()).isNull();
    }

    @Test
    @DisplayName("저장되어있는 댓글을 수정한다.")
    @WithMockUser
    @Transactional
    void commentModify() {
        //given
        createPost();
        Long commentId = createComment();
        String newComment = "수정됨";

        //when
        commentService.modifyComment(commentId, newComment, 1L);
        Comment modifiedComment = commentService.getComment(commentId);

        //then
        assertThat(modifiedComment.getContent()).isEqualTo("수정됨");
    }

    @DisplayName("댓글을 삭제한다.")
    @Test
    @Transactional
    void deleteComment() {
        //given
        Long postId = createPost();
        Long commentId = createComment();

        //when
        commentService.deleteComment(commentId, 1L);
        Comment deletedComment = commentService.getComment(commentId);

        //then
        assertThat(deletedComment.isDeleted()).isTrue();
    }


}
