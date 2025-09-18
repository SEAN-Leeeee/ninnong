<template>
  <div class="comment-thread">
    <!-- Parent Comment -->
    <div class="comment-item" :class="{ 'is-deleted': comment.deleted }">
      <div class="comment-avatar" aria-hidden="true">
        <div class="avatar-fallback">{{ authorInitial(comment.writer) }}</div>
      </div>

      <div class="comment-main">
        <div class="comment-meta">
          <strong class="author">{{ displayAuthor(comment.writer) }}</strong>
          <span class="meta-sep">·</span>
          <time class="date">{{ formatDate(comment.createdAt) }}</time>

          <button
              v-if="isOwner(comment.writer) && !comment.deleted"
              class="menu-button"
              aria-label="more actions"
              @click="toggleMenu"
          >⋯</button>

          <div v-if="menuOpen" class="menu-dropdown">
            <button class="menu-item" @click="startEdit">수정</button>
            <button class="menu-item danger" @click="confirmDelete">삭제</button>
          </div>
        </div>

        <div class="comment-content">
          <template v-if="!isEditing">
            {{ comment.content }}
          </template>
          <template v-else>
            <textarea
                v-model="editText"
                class="edit-textarea"
                rows="3"
                placeholder="댓글 수정..."
            ></textarea>
            <div class="edit-actions">
              <button class="btn-primary" @click="submitEdit">수정 완료</button>
              <button class="btn-link" @click="cancelEdit">취소</button>
            </div>
          </template>
        </div>

        <div class="comment-toolbar" v-if="!comment.deleted">
          <button class="btn-link" @click="toggleReply">답글</button>
        </div>

        <div v-if="showReply && !comment.deleted" class="reply-box">
          <textarea
              v-model="replyText"
              class="reply-textarea"
              placeholder="답글 작성..."
              rows="3"
          ></textarea>
          <div class="reply-actions">
            <button class="btn-primary" @click="submitReply">답글 작성</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Child Comments -->
    <div class="children" v-if="comment.children && comment.children.length > 0">
      <div
          class="comment-item is-child"
          :class="{ 'is-deleted': child.deleted }"
          v-for="child in comment.children"
          :key="child.id"
      >
        <div class="comment-avatar" aria-hidden="true">
          <div class="avatar-fallback">{{ authorInitial(child.writer) }}</div>
        </div>
        <div class="comment-main">
          <div class="comment-meta">
            <strong class="author">{{ displayAuthor(child.writer) }}</strong>
            <span class="meta-sep">·</span>
            <time class="date">{{ formatDate(child.createdAt) }}</time>
          </div>
          <div class="comment-content">
            {{ child.content }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useUserStore } from '@stores/user.js'

export default {
  name: 'CommentItem',
  props: {
    comment: { type: Object, required: true },
  },
  emits: ['add-reply', 'delete-comment', 'edit-comment'],
  data() {
    return {
      userStore: useUserStore(),
      menuOpen: false,
      isEditing: false,
      editText: '',
      showReply: false,
      replyText: '',
    }
  },
  methods: {
    isOwner(writerId) {
      return this.userStore.currentUser && this.userStore.currentUser.id === writerId;
    },
    displayAuthor(writerId) {
      if (this.comment.deleted) return '알 수 없음';
      if (this.isOwner(writerId)) {
        return this.userStore.currentUser.nickname || '나';
      }
      // TODO: 백엔드에서 작성자 닉네임을 제공하거나, writerId로 유저 정보를 조회하는 API가 필요합니다.
      return `익명`;
    },
    authorInitial(writerId) {
      const authorName = this.displayAuthor(writerId);
      return authorName.charAt(0).toUpperCase();
    },
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return d.toLocaleString('ko-KR', { dateStyle: 'short', timeStyle: 'short' });
    },
    toggleMenu() {
      this.menuOpen = !this.menuOpen;
    },
    toggleReply() {
      this.showReply = !this.showReply;
      this.menuOpen = false;
    },
    startEdit() {
      this.isEditing = true;
      this.editText = this.comment.content;
      this.menuOpen = false;
    },
    cancelEdit() {
      this.isEditing = false;
      this.editText = '';
    },
    submitEdit() {
      if (!this.editText.trim()) return;
      this.$emit('edit-comment', { commentId: this.comment.id, content: this.editText.trim() });
      this.cancelEdit();
    },
    confirmDelete() {
      this.$emit('delete-comment', this.comment.id);
      this.menuOpen = false;
    },
    submitReply() {
      if (!this.replyText.trim()) return;
      this.$emit('add-reply', { parentId: this.comment.id, content: this.replyText.trim() });
      this.replyText = '';
      this.showReply = false;
    },
  }
}
</script>

<style scoped>
/* 기존 스타일 유지, 필요한 경우 하단에 추가 */
.comment-thread {
  margin-bottom: 1rem;
}

.is-child {
  margin-left: 48px; /* 24px (indent) + 24px (avatar) */
  background-color: #f8f9fa;
  padding: 0.75rem;
  border-radius: 8px;
  margin-top: 0.75rem;
}

.is-child .comment-main {
  width: 100%;
}
</style>
