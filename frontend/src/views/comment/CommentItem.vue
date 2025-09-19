<template>
  <div class="comment-thread" :class="{ 'own-thread': isOwner(comment.writerId) }">
    <!-- Parent Comment -->
    <div class="comment-item" :class="{ 'is-deleted': comment.isDeleted, 'is-own': isOwner(comment.writerId) }">

      <div class="comment-main">
        <div class="comment-meta" :data-menu-id="comment.id">
          <strong class="author">{{ displayAuthor(comment.isDeleted, comment.writerNickname) }}</strong>
          <span class="meta-sep">·</span>
          <time class="date">{{ formatDate(comment.createdAt) }}</time>
          <span class="meta-spacer"></span>
          <button
              v-if="!comment.isDeleted && !comment.parentId"
              class="btn-reply"
              @click="toggleReply"
          >답글</button>
          <button
              v-if="isOwner(comment.writerId) && !comment.isDeleted"
              class="menu-button"
              aria-label="more actions"
              @click.stop="toggleMenu(comment.id)"
          >⋯</button>
          <div v-if="menuOpenFor === comment.id" class="menu-dropdown" @click.stop>
            <button class="menu-item" @click="startEdit(comment)">수정 </button>
            <button class="menu-item danger" @click="confirmDelete(comment.id)">삭제</button>
          </div>
        </div>

        <div class="comment-content">
          <template v-if="editingComment?.id !== comment.id">
            {{ comment.content }}
          </template>
          <template v-else>
            <textarea
                v-model="editingComment.content"
                class="edit-textarea"
                rows="3"
                placeholder="댓글 수정..."
            ></textarea>
            <div class="edit-actions">
              <button class="btn-primary" @click="submitEdit">수정</button>
              <button class="btn-secondary" @click="cancelEdit">취소</button>
            </div>
          </template>
        </div>

        <div v-if="showReply && !comment.isDeleted" class="reply-box">
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
    <div class="children" v-if="comment.children && comment.children.length > 0" >
      <div
          class="comment-item is-child"
          :class="{ 'is-deleted': child.idDeleted, 'is-own': isOwner(child.writer) }"
          v-for="child in comment.children"
          :key="child.id"
      >
        <div class="comment-main">
          <div class="comment-meta" :data-menu-id="child.id">
            <strong class="author">{{ displayAuthor(child.idDeleted, child.writerNickname) }}</strong>
            <span class="meta-sep">·</span>
            <time class="date">{{ formatDate(child.createdAt) }}</time>
            <span class="meta-spacer"></span>
            <button
                v-if="isOwner(child.writer) && !child.idDeleted"
                class="menu-button"
                aria-label="more actions"
                @click.stop="toggleMenu(child.id)"
            >⋯</button>
            <div v-if="menuOpenFor === child.id" class="menu-dropdown" @click.stop>
              <button class="menu-item" @click="startEdit(child)">수정</button>
              <button class="menu-item danger" @click="confirmDelete(child.id)">삭제</button>
            </div>
          </div>
          <div class="comment-content">
            <template v-if="editingComment?.id !== child.id">
              {{ child.content }}
            </template>
            <template v-else>
              <textarea
                  v-model="editingComment.content"
                  class="edit-textarea"
                  rows="3"
                  placeholder="답글 수정..."
              ></textarea>
              <div class="edit-actions">
                <button class="btn-primary" @click="submitEdit">수정</button>
                <button class="btn-secondary" @click="cancelEdit">취소</button>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useUserStore } from '@stores/user.js';
import { defineComponent, ref, computed, onMounted, onBeforeUnmount } from 'vue';

export default defineComponent({
  name: 'CommentItem',
  props: {
    comment: { type: Object, required: true },
  },
  emits: ['add-reply', 'delete-comment', 'edit-comment'],
  setup(props, { emit }) {
    const userStore = useUserStore();
    const user = computed(() => userStore.currentUser);

    const menuOpenFor = ref(null); // Holds the ID of the comment whose menu is open
    const editingComment = ref(null); // Holds the comment object being edited
    const showReply = ref(false);
    const replyText = ref('');

    const isOwner = (writerId) => {
      const me = user.value?.id;
      if (me == null || writerId == null) return false;
      // The writerId might be an object { id: ... } in some cases.
      const ownerId = typeof writerId === 'object' ? writerId.id : writerId;
      return String(me) === String(ownerId);
    };

    const displayAuthor = (isDeleted, writerNickname) => {
      return isDeleted ? '알 수 없음' : writerNickname;
    };

    const formatDate = (date) => {
      if (!date) return '';
      return new Date(date).toLocaleString('ko-KR', { dateStyle: 'short', timeStyle: 'short' });
    };

    const toggleMenu = (commentId) => {
      menuOpenFor.value = menuOpenFor.value === commentId ? null : commentId;
    };

    const closeMenu = () => {
      menuOpenFor.value = null;
    };

    const startEdit = (comment) => {
      editingComment.value = { ...comment, originalContent: comment.content };
      closeMenu();
    };

    const cancelEdit = () => {
      editingComment.value = null;
    };

    const submitEdit = () => {
      if (!editingComment.value || !editingComment.value.content.trim()) return;
      emit('edit-comment', {
        commentId: editingComment.value.id,
        content: editingComment.value.content.trim(),
      });
      cancelEdit();
    };

    const confirmDelete = (commentId) => {
      emit('delete-comment', commentId);
      closeMenu();
    };

    const toggleReply = () => {
      showReply.value = !showReply.value;
      closeMenu();
    };

    const submitReply = () => {
      if (!replyText.value.trim()) return;
      emit('add-reply', {
        parentId: props.comment.id,
        content: replyText.value.trim(),
      });
      replyText.value = '';
      showReply.value = false;
    };

    // Close menu if clicking outside
    const handleOutsideClick = (event) => {
      if (menuOpenFor.value) {
        const menuElement = document.querySelector(`[data-menu-id="${menuOpenFor.value}"]`);
        if (menuElement && !menuElement.contains(event.target)) {
          closeMenu();
        }
      }
    };

    onMounted(() => {
      document.addEventListener('click', handleOutsideClick, true);
    });

    onBeforeUnmount(() => {
      document.removeEventListener('click', handleOutsideClick, true);
    });

    return {
      user,
      menuOpenFor,
      editingComment,
      showReply,
      replyText,
      isOwner,
      displayAuthor,
      formatDate,
      toggleMenu,
      startEdit,
      cancelEdit,
      submitEdit,
      confirmDelete,
      toggleReply,
      submitReply,
    };
  },
});
</script>

<style scoped>
.comment-thread {
  margin: 0;
  padding: 12px 0;
  border-top: 1px solid #e5e7eb; /* separator between threads */
}
/* No separator before the very first root thread */
.comment-thread:first-of-type {
  border-top: none;
  padding-top: 0;
}

/* Parent & child share the same flat look */
.comment-item {
  background: transparent;
  border-radius: 0;
  padding: 0;
  margin: 0 0 8px 0;
  transition: none;
  width: 100%;
}
.own-thread {
  background: #f5faff;
  border-radius: 0;
}
.own-thread:hover { background: #f5faff !important; }

.comment-item:hover {
  background: transparent !important;
}

/* Common layout */
.comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
  flex-shrink: 0;
  overflow: hidden;
}
.avatar-fallback {
  font-size: 12px;
  color: #475569;
  font-weight: 700;
}

.comment-main {
  width: 100%;
}

.comment-item {
  display: flex;
  gap: 12px;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 6px;
  position: relative; /* anchor for absolute dropdown */
  z-index: 1;         /* keep above content */
}

/* Push actions (reply/menu) to the right side of the first line */
.meta-spacer { margin-left: auto; }

/* Right-side reply button: neutral, no hover color change */
.btn-reply {
  background: transparent;
  border: none;
  color: #4b5563;         /* neutral gray like the screenshot */
  font-size: 14px;
  cursor: pointer;
  padding: 0 6px;
  line-height: 1;
}
.btn-reply:hover { color: #4b5563; background: transparent; }

/* Slight highlight for my own comments */
.is-own {
  background: #f5faff;    /* subtle blue tint */
  padding: 12px;
}
.is-own:hover { background: #f5faff !important; }

.author {
  color: #111827;
  font-weight: 600;
  font-size: 14px;
}

.date {
  color: #9ca3af;
  font-variant-numeric: tabular-nums;
}

.menu-button {
  border: none;
  background: transparent;
  color: #000;
  cursor: pointer;
  font-size: 18px;
  line-height: 1;
  padding: 4px 6px;
  position: relative;
  z-index: 2;
}

.menu-dropdown {
  position: absolute;
  margin-top: 28px;
  right: 0;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 6px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  z-index: 1000; /* ensure visible above containers */
  box-shadow: 0 6px 20px rgba(0,0,0,0.12);
}
.menu-item {
  background: transparent;
  border: none;
  text-align: left;
  padding: 6px 8px;
  font-size: 13px;
  cursor: pointer;
  color: #000;
}
.menu-item.danger { color: #dc2626; }

.comment-content {
  font-size: 14px;
  color: #111827;
  white-space: pre-wrap;
  word-break: break-word;
}

.edit-textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 14px;
}
.edit-actions {
  margin-top: 6px;
  display: flex;
  gap: 8px;
  justify-content: flex-end; /* Align buttons to the right */
}
/* Unified button base — identical shape/size/typography */
.btn-primary,
.btn-secondary {
  border: 1px solid transparent;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 13px;
  cursor: pointer;
  min-width: 64px;
  text-align: center;
  line-height: 1.2;
  font-weight: 600;
}
/* Primary (수정) — blue */
.btn-primary {
  background: #2563eb;
  color: #fff;
  border-color: #2563eb;
}
/* Secondary (취소) — white */
.btn-secondary {
  background: #ffffff;
  color: #111827;
  border-color: #d1d5db;
}
.btn-secondary:hover {
  background: #f9fafb;
}

/* Reply box */
.reply-box {
  margin-top: 8px;
}
.reply-textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 14px;
}
.reply-actions {
  margin-top: 6px;
  display: flex;
  justify-content: flex-end; /* Align button to the right */
}

/* Children: indent only — no background and no hover color */
.children {
  margin-left: 40px;
  margin-top: 6px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.children .comment-item {
  border: none !important;
}
.children {
  border: none;
}
.is-child {
  background: transparent;
  padding: 0;
  border-radius: 0;
  margin: 0;
}
.is-child:hover { background: transparent; }

/* 내가 쓴 답댓글 강조 */
.is-child.is-own {
  background: #f5faff;
  padding: 12px;
}

/* Deleted look */
.is-deleted .comment-content { color: #9ca3af; }
</style>
/* Root thread block: draw a single separator between root comments */

