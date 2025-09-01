<!-- src/components/CommentSection.vue -->
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
        :currentUser="currentUser"
        @reply-submitted="emitReply"
        @comment-deleted="emitDelete"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CommentItem from './CommentItem.vue'

const props = defineProps({
  comments: Array,
  currentUser: String
})
const emit = defineEmits(['comment-added', 'reply-submitted', 'comment-deleted'])

const newComment = ref('')

const submitComment = () => {
  if (!newComment.value.trim()) return
  const newCmt = {
    id: Date.now(),
    author: props.currentUser,
    content: newComment.value,
    createdAt: new Date().toISOString(),
    children: []
  }
  emit('comment-added', newCmt)
  newComment.value = ''
}

const emitReply = (parentId, reply) => {
  emit('reply-submitted', parentId, reply)
}

const emitDelete = (id) => {
  emit('comment-deleted', id)
}
</script>
