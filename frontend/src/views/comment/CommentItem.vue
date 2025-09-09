<!-- src/components/CommentItem.vue -->
<template>
  <div class="comment-item" :style="{ marginLeft: depth * 24 + 'px' }">
    <div class="comment-row">
      <div class="comment-avatar" aria-hidden="true">
        <div class="avatar-fallback">{{ authorInitial }}</div>
      </div>

      <div class="comment-main">
        <div class="comment-meta">
          <strong class="author">{{ comment.author }}</strong>
          <span class="meta-sep">·</span>
          <time class="date">{{ formatDate(comment.createdAt) }}</time>
        </div>

        <div class="comment-content">{{ comment.content }}</div>

        <div class="comment-toolbar">
          <button class="btn-link" @click="toggleReply">답글</button>
          <span v-if="isOwner" class="meta-sep">·</span>
          <button v-if="isOwner" class="btn-link" @click="deleteComment">삭제</button>
        </div>

        <div v-if="showReply" class="reply-box">
          <textarea
            v-model="replyText"
            class="reply-textarea"
            placeholder="답글 작성..."
            rows="3"
          ></textarea>
          <div class="reply-actions">
            <button class="btn-primary" @click="submitReply">작성</button>
          </div>
        </div>

        <div class="children">
          <CommentItem
            v-for="child in comment.children"
            :key="child.id"
            :comment="child"
            :depth="depth + 1"
            @reply-submitted="$emit('reply-submitted', ...arguments)"
            @comment-deleted="$emit('comment-deleted', $event)"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useUserStore } from '@stores/user.js'
import api from '@/axios.js'

export default {
  name: 'CommentItem',
  props: {
    comment: { type: Object, required: true },
    depth: { type: Number, default: 0 }
  },
  emits: ['reply-submitted', 'comment-deleted'],
  data() {
    return {
      showReply: false,
      replyText: '',
      userStore: useUserStore()
    }
  },
  computed: {
    currentUser() {
      const u = this.userStore.currentUser || {}
      return u.username || u.name || ''
    },
    isOwner() {
      return (this.comment && this.comment.author) === this.currentUser
    },
    authorInitial() {
      const a = (this.comment && this.comment.author) ? String(this.comment.author).trim() : '?'
      return a.charAt(0).toUpperCase()
    }
  },
  mounted() {
    // 필요 시 초기 로딩 로직을 이곳에 배치 (예: 권한 체크, 로깅 등)
    // console.debug('[CommentItem] mounted:', this.comment?.id)
  },
  methods: {
    formatDate(date) {
      const d = new Date(date)
      const yyyy = d.getFullYear()
      const mm = String(d.getMonth() + 1).padStart(2, '0')
      const dd = String(d.getDate()).padStart(2, '0')
      const hh = String(d.getHours()).padStart(2, '0')
      const mi = String(d.getMinutes()).padStart(2, '0')
      return `${yyyy}-${mm}-${dd} ${hh}:${mi}`
    },
    toggleReply() {
      this.showReply = !this.showReply
    },
    submitReply() {
      if (!this.replyText.trim()) return
      this.$emit('reply-submitted', this.comment.id, {
        id: Date.now(),
        author: this.currentUser,
        content: this.replyText,
        createdAt: new Date().toISOString(),
        children: []
      })
      this.replyText = ''
      this.showReply = false
    },
    deleteComment() {
      this.$emit('comment-deleted', this.comment.id)
    }
  }
}
</script>

<!-- 스타일은 전역 global.css에서 관리합니다. 클래스 구조만 제공합니다. -->
