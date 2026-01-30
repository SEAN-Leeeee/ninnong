package com.sean.ninnong.comment.service;

import com.sean.ninnong.comment.dto.ChildCommentResponse;
import com.sean.ninnong.comment.dto.CommentRequest;
import com.sean.ninnong.comment.dto.CommentThreadResponse;
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

import java.util.List;

import static com.sean.ninnong.common.enums.Category.FREE;
import static org.assertj.core.api.Assertions.assertThat;
@ActiveProfiles("test")
@SpringBootTest
class CommentQueryServiceImplTest {

    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentQueryService commentQueryService;

    Long createComment(String content, Long parentId, Long postId) {
        return  commentService.createComment(CommentRequest.builder()
                .content(content)
                .postId(postId)
                .parentId(parentId)
                .isDeleted(false)
                .build(), 1L);
    }

    Long createPost() {
        return postService.createPost(PostRequest.builder()
                .category(FREE)
                .subject(PostSubject.FREE)
                .title("테스트")
                .content("테스트컨텐츠입니다다다닫")
                .imageUrls(null)
                .build(), 1L);
    }

    @DisplayName("children이 없는 comments를 불러온다.")
    @Test
    @WithMockUser
    @Transactional
    void findByCommentByPostId() {
        //given
        Long postId = createPost();
        Long commentId = createComment("content", null, postId);
        //when
        List<CommentThreadResponse> threads = commentQueryService.findCommentThreads(postId);

        //then
        assertThat(threads.get(0).id()).isNotNull();
        assertThat(threads).hasSize(1);
        assertThat(threads.get(0)).extracting("content", "isDeleted")
                .contains("content", false);
        assertThat(threads.get(0).children()).isEmpty();
    }

    @DisplayName("children이 있는 댓글을 불러온다.")
    @Test
    void findRootComment() {
        //given
        Long postId = createPost();
        Long rootCommentId = createComment("content", null, postId);
        Long childCommentId = createComment("childComment", rootCommentId, postId);

        //when
        List<CommentThreadResponse> commentThreads = commentQueryService.findCommentThreads(postId);

        //then
        assertThat(commentThreads).hasSize(1);
        assertThat(commentThreads.get(0).children()).hasSize(1);
        List<ChildCommentResponse> children = commentThreads.get(0).children();
        assertThat(children.get(0).parentId()).isEqualTo(rootCommentId);
    }

    @DisplayName("root와 child가 모두 삭제된 댓글을 불러온다.")
    @Test
    @Transactional
    void findDeletedRootAndChild() {
        //given
        Long postId = createPost();
        Long rootCommentId = createComment("content", null, postId);
        Long childCommentId = createComment("childComment", rootCommentId, postId);

        commentService.deleteComment(rootCommentId, 1L);
        commentService.deleteComment(childCommentId, 1L);

        //when
        List<CommentThreadResponse> commentThreads = commentQueryService.findCommentThreads(postId);

        //then
        assertThat(commentThreads.get(0).content()).isEqualTo("삭제된 댓글입니다");
        assertThat(commentThreads.get(0).children().get(0).content()).isEqualTo("삭제된 댓글입니다");
    }
}
