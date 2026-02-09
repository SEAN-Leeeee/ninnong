<template>
  <div class="comment-wrapper">
    <h3 class="comment-count">댓글 {{ totalCommentCount }}</h3>

    <div v-if="isLoading" class="loading-message">댓글을 불러오는 중...</div>
    <div v-if="error" class="error-message">{{ error }}</div>

    <div v-if="!isLoading && !error && comments.length > 0">
      <CommentItem
          v-for="comment in comments"
          :key="comment.id"
          :comment="comment"
          @add-reply="handleAddReply"
          @delete-comment="handleDeleteComment"
          @edit-comment="handleEditComment"
      />
    </div>

    <div v-if="!isLoading && !error && comments.length === 0" class="no-comments-message">
      아직 댓글이 없습니다. 첫 댓글을 작성해보세요!
    </div>

    <div class="comment-input-wrapper">
      <textarea v-model="newComment" class="comment-textarea" placeholder="댓글을 입력해주세요"></textarea>
      <button class="comment-submit-btn" @click="submitComment" :disabled="isLoading">작성</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, defineProps } from 'vue';
import api from '@/axios.js';
import CommentItem from './CommentItem.vue';
import { useUserStore } from '@/stores/user.js';

const props = defineProps({
  postId: { type: Number, required: true },
});

const comments = ref([]);
const newComment = ref('');
const isLoading = ref(false);
const error = ref(null);

const totalCommentCount = computed(() => {
  if (!comments.value) return 0;
  return comments.value.reduce((count, comment) => {
    return count + 1 + (comment.children ? comment.children.length : 0);
  }, 0);
});

const loadComments = async () => {
  if (!props.postId) return;
  isLoading.value = true;
  error.value = null;
  try {
    const res = await api.get(`/comments/${props.postId}`);
    comments.value = res.data;
  } catch (e) {
    console.error('댓글을 불러오는 중 오류가 발생했습니다.', e);
    error.value = '댓글을 불러올 수 없습니다.';
  } finally {
    isLoading.value = false;
  }
};

onMounted(loadComments);

const submitComment = async () => {
  if (!newComment.value.trim()) return;
  await handleAddComment(newComment.value.trim());
  newComment.value = '';
};

const handleAddComment = async (content) => {
  try {
    const res = await api.post(`/comments/${props.postId}`, {
      content,
      postId: props.postId,
      parentId: null,
    });

    const newCommentFromServer = res.data;
    const currentUser = userStore.currentUser;

    const newEntry = {
      id: newCommentFromServer.id,
      content: newCommentFromServer.content || content,
      createdAt: newCommentFromServer.createdAt || new Date().toISOString(),
      writerId: currentUser.id,
      writerNickname: currentUser.nickname,
      isDeleted: false,
      children: [],
    };

    comments.value.push(newEntry);
  } catch (e) {
    console.error('댓글 작성 중 오류가 발생했습니다.', e);
  }
};

const userStore = useUserStore();

const handleAddReply = async ({ parentId, content }) => {
  try {
    const res = await api.post(`/comments/${props.postId}`, {
      content,
      postId: props.postId,
      parentId,
    });

    // Manually construct the new child comment to ensure its structure is correct.
    const newCommentFromServer = res.data;
    const currentUser = userStore.currentUser;

    if (!newCommentFromServer || !newCommentFromServer.id || !currentUser) {
      // If we don't get the expected data, fall back to a full reload.
      throw new Error("Invalid data received from server");
    }

    const newChild = {
      id: newCommentFromServer.id,
      content: newCommentFromServer.content || content,
      createdAt: newCommentFromServer.createdAt || new Date().toISOString(),
      writer: { id: currentUser.id }, // The template expects a 'writer' object with an 'id'
      writerNickname: currentUser.nickname,
      idDeleted: false,
      children: [],
    };

    const parentComment = comments.value.find(c => c.id === parentId);
    if (parentComment) {
      if (!parentComment.children) {
        parentComment.children = [];
      }
      parentComment.children.push(newChild);
    }

  } catch (e) {
    console.error('답글 작성 중 오류가 발생했습니다. 낙관적 업데이트에 실패하여 전체 목록을 다시 불러옵니다.', e);
    // Fallback to reloading all comments if the optimistic update fails.
    await loadComments();
  }
};

const handleEditComment = async ({ commentId, content }) => {
  try {
    await api.patch(`/comments/${commentId}`, null, { params: { newContent: content } });
    // Optimistic update: Find and update the comment
    const findAndUpdate = (commentList) => {
      for (const comment of commentList) {
        if (comment.id === commentId) {
          comment.content = content;
          return true;
        }
        if (comment.children && findAndUpdate(comment.children)) {
          return true;
        }
      }
      return false;
    };
    findAndUpdate(comments.value);
  } catch (e) {
    console.error('댓글 수정 중 오류가 발생했습니다.', e);
  }
};

const handleDeleteComment = async (commentId) => {
  if (!confirm('정말로 이 댓글을 삭제하시겠습니까?')) return;

  try {
    await api.delete(`/comments/${commentId}`);

    // Revised optimistic update logic for deletion
    const findAndMarkAsDeleted = (comments) => {
      for (const comment of comments) {
        if (comment.id === commentId) {
          // Always mark as deleted, never remove from the list.
          // This ensures the "deleted" message can be displayed.
          comment.isDeleted = true;
          comment.idDeleted = true; // Set both properties to handle data inconsistency
          comment.content = '삭제된 댓글입니다.';
          return true;
        }
        if (comment.children && findAndMarkAsDeleted(comment.children)) {
          return true;
        }
      }
      return false;
    };

    findAndMarkAsDeleted(comments.value);

  } catch (e) {
    console.error('댓글 삭제 중 오류가 발생했습니다.', e);
  }
};
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
  margin-top: 40px; /* Add space above the input form */
  margin-bottom: 0; /* Remove bottom margin as it's now at the end */
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

/* Added from previous refactoring for feedback messages */
.loading-message,
.error-message,
.no-comments-message {
  padding: 20px;
  text-align: center;
  color: #6b7280;
  background-color: #f9fafb;
  border-radius: 8px;
  margin: 20px 0;
}

.error-message {
  color: #ef4444;
  background-color: #fef2f2;
}
</style>
