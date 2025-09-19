<template>
  <div class="comment-wrapper">
    <h3 class="comment-count">댓글 {{ totalCommentCount }}</h3>

    <div class="comment-input-wrapper">
      <textarea v-model="newComment" class="comment-textarea" placeholder="댓글을 입력해주세요"></textarea>
      <button class="comment-submit-btn" @click="submitComment">작성</button>
    </div>

    <CommentItem
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
        @add-reply="handleAddReply"
        @delete-comment="handleDeleteComment"
        @edit-comment="handleEditComment"
    />
  </div>
</template>

<script>
import api from '@/axios.js';
import CommentItem from './CommentItem.vue';
import { useUserStore } from '@stores/user.js';

export default {
  name: 'CommentSection',
  components: { CommentItem },
  props: {
    postId: { type: Number, required: true },
  },
  data() {
    return {
      newComment: '',
      comments: [],
    }
  },
  computed: {
    totalCommentCount() {
      if (!this.comments) return 0;
      return this.comments.reduce((count, comment) => {
        return count + 1 + (comment.children ? comment.children.length : 0);
      }, 0);
    }
  },
  async mounted() {
    await this.loadComments();
  },
  methods: {
    async loadComments() {
      if (!this.postId) return;
      try {
        const res = await api.get(`/comments/${this.postId}`)
        this.comments = res.data
      } catch (e) {
        console.error('댓글을 불러오는 중 오류가 발생했습니다.', e)
      }
    },
    submitComment() {
      if (!this.newComment.trim()) {
        alert('댓글 내용을 입력해주세요.');
        return;
      }
      this.handleAddComment(this.newComment.trim());
      this.newComment = '';
    },
    async handleAddComment(content) {
      try {
        await api.post(`/comments/${this.postId}`, {
          content,
          postId: this.postId,
          parentId: null,
        })
        await this.loadComments()
      } catch (e) {
        console.error('댓글 작성 중 오류가 발생했습니다.', e)
        alert('댓글을 작성할 수 없습니다.');
      }
    },
    async handleAddReply({ parentId, content }) {
      try {
        await api.post(`/comments/${this.postId}`, {
          content,
          postId: this.postId,
          parentId,
        })
        await this.loadComments()
      } catch (e) {
        console.error('답글 작성 중 오류가 발생했습니다.', e)
        alert('답글을 작성할 수 없습니다.')
      }
    },
    async handleEditComment({ commentId, content }) {
      try {
        await api.patch(`/comments/${commentId}`, null, {
          params: { newContent: content }
        })
        await this.loadComments()
      } catch (e) {
        console.error('댓글 수정 중 오류가 발생했습니다.', e)
        alert('댓글을 수정할 수 없습니다.')
      }
    },
    async handleDeleteComment(commentId) {
      if (!confirm('정말로 이 댓글을 삭제하시겠습니까?')) return;
      try {
        await api.delete(`/comments/${commentId}`)
        await this.loadComments()
      } catch (e) {
        console.error('댓글 삭제 중 오류가 발생했습니다.', e)
        alert('댓글을 삭제할 수 없습니다.')
      }
    },
  }
}
</script>

<style scoped>
/* Section spacing */
.comment-wrapper {
  margin-top: 32px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

/* Flat look — no hover color on items */
.comment-item:hover { background: transparent !important; }
.is-child { background: transparent !important; }
.is-child:hover { background: transparent !important; }

/* Input area */
.comment-input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}
.comment-textarea {
  width: 100%;
  min-height: 80px;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
}
.comment-submit-btn {
  align-self: flex-end;
  background: #2563eb;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 700;
  cursor: pointer;
}
</style>
