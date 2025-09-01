<!-- src/components/CommentItem.vue -->
<template>
  <div class="comment-item" :style="{ marginLeft: depth * 24 + 'px' }">
    <div class="comment-header">
      <strong>{{ comment.author }}</strong>
      <span class="date">{{ formatDate(comment.createdAt) }}</span>
    </div>
    <div class="comment-body">{{ comment.content }}</div>

    <div class="comment-actions">
      <button @click="showReply = !showReply">답글</button>
      <button v-if="comment.author === currentUser" @click="deleteComment">삭제</button>
    </div>

    <div v-if="showReply" class="reply-input">
      <textarea v-model="replyText" placeholder="답글 작성..."></textarea>
      <button @click="submitReply">작성</button>
    </div>

    <CommentItem
        v-for="child in comment.children"
        :key="child.id"
        :comment="child"
        :depth="depth + 1"
        :currentUser="currentUser"
        @reply-submitted="$emit('reply-submitted', ...arguments)"
        @comment-deleted="$emit('comment-deleted', $event)"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  comment: Object,
  depth: Number,
  currentUser: String
})
const emit = defineEmits(['reply-submitted', 'comment-deleted'])

const showReply = ref(false)
const replyText = ref('')

const formatDate = (date) => {
  const d = new Date(date)
  return `${d.toLocaleDateString()} ${d.toLocaleTimeString().slice(0, 5)}`
}

const submitReply = () => {
  if (!replyText.value.trim()) return
  emit('reply-submitted', props.comment.id, {
    id: Date.now(),
    author: props.currentUser,
    content: replyText.value,
    createdAt: new Date().toISOString(),
    children: []
  })
  replyText.value = ''
  showReply.value = false
}

const deleteComment = () => {
  emit('comment-deleted', props.comment.id)
}
</script>
