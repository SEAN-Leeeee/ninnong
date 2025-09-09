<template>
  <div class="comment-wrapper">
    <h3 class="comment-count">댓글 {{ comments.length }}</h3>

    <div class="comment-input-wrapper">
      <textarea v-model="newComment" class="comment-textarea" placeholder="댓글을 입력해주세요" />
      <button class="comment-submit-btn" @click="submitComment">작성</button>
    </div>

    <CommentItem
        v-for="comment in comments"
        :key="comment.id"
        :comment="comment"
        :depth="0"
        @reply-submitted="emitReply"
        @comment-deleted="emitDelete"
    />
  </div>
</template>

<script>
import CommentItem from './CommentItem.vue'
import { useUserStore } from '@stores/user.js'
import api from "@/axios.js";

export default {
  name: 'CommentSection',
  components: { CommentItem },
  props: {
    comments: { type: Array, default: () => [] }
  },
  data() {
    return {
      newComment: ''
    }
  },
  computed: {
    user() {
      const userStore = useUserStore()
      return userStore.currentUser
    }
  },
  methods: {
    submitComment() {
      if (!this.newComment.trim()) return
      const newCmt = {
        id: Date.now(),
        author: this.user,
        content: this.newComment,
        createdAt: new Date().toISOString(),
        children: []
      }
      this.$emit('comment-added', newCmt)
      this.newComment = ''
    },
    emitReply(parentId, reply) {
      this.$emit('reply-submitted', parentId, reply)
    },
    emitDelete(id) {
      this.$emit('comment-deleted', id)
    }
  }
}
</script>
