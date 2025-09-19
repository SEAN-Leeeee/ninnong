<template>
  <div class="comment-thread" :class="{ 'own-thread': isOwner(comment.writerId) }">
    <!-- Parent Comment -->
    <div class="comment-item" :class="{ 'is-deleted': comment.isDeleted, 'is-own': isOwner(comment.writerId) }">

      <div class="comment-main">
        <div class="comment-meta">
          <strong class="author">{{ displayAuthor(comment.isDeleted, comment.writerNickname) }}</strong>
          <span class="meta-sep">·</span>
          <time class="date">{{ formatDate(comment.createdAt) }}</time>
          <span class="meta-spacer"></span>
          <button
              v-if="!comment.isDeleted"
              class="btn-reply"
              @click="toggleReply"
          >답글</button>
          <button
              v-if="isOwner(comment.writerId) && !comment.isDeleted"
              class="menu-button"
              aria-label="more actions"
              @click.stop="toggleMenu"
          >⋯</button>
          <div v-if="menuOpen" class="menu-dropdown" @click.stop>
            <button class="menu-item" @click="startEdit">수정 </button>
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
          :class="{ 'is-deleted': child.isDeleted, 'is-own': isOwner(child.writer) }"
          v-for="child in comment.children"
          :key="child.id"
          :data-id="child.id"
      >
        <div class="comment-main">
          <div class="comment-meta">
            <strong class="author">{{ displayAuthor(child.isDeleted, child.writerNickname) }}</strong>
            <span class="meta-sep">·</span>
            <time class="date">{{ formatDate(child.createdAt) }}</time>
            <span class="meta-spacer"></span>
            <button
                v-if="isOwner(child.writer) && !child.isDeleted"
                class="menu-button"
                aria-label="more actions"
                @click.stop="toggleChildMenu(child.id)"
            >⋯</button>
            <div v-if="childMenus[child.id]" class="menu-dropdown" @click.stop>
              <button class="menu-item" @click="startChildEdit(child)">수정</button>
              <button class="menu-item danger" @click="confirmChildDelete(child)">삭제</button>
            </div>
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
      menuOpen: false,
      childMenus: {},
      isEditing: false,
      editText: '',
      showReply: false,
      replyText: '',
    }
  },
  computed: {
    user() {
      const userStore = useUserStore()
      return userStore.currentUser
    },
  },
  methods: {
    handleOutsideClick(event) {
      // Close parent menu if click is outside both button and dropdown
      if (this.menuOpen) {
        const parentRoot = this.$el.querySelector('.comment-item:not(.is-child)');
        const parentBtn  = parentRoot ? parentRoot.querySelector('.menu-button') : null;
        const parentMenu = parentRoot ? parentRoot.querySelector('.menu-dropdown') : null;
        const isInsideParent = (parentBtn && parentBtn.contains(event.target)) ||
                               (parentMenu && parentMenu.contains(event.target));
        if (!isInsideParent) this.menuOpen = false;
      }
      // Close open child menus if click is outside
      for (const id in this.childMenus) {
        if (!this.childMenus[id]) continue;
        const childRoot = this.$el.querySelector(`.comment-item.is-child[data-id="${id}"]`);
        const childBtn  = childRoot ? childRoot.querySelector('.menu-button') : null;
        const childMenu = childRoot ? childRoot.querySelector('.menu-dropdown') : null;
        const isInsideChild = (childBtn && childBtn.contains(event.target)) ||
                              (childMenu && childMenu.contains(event.target));
        if (!isInsideChild) {
          if (this.$set) this.$set(this.childMenus, id, false);
          else this.childMenus[id] = false;
        }
      }
    },
    isOwner(writerId) {
      const me = this.user?.id;
      if (me == null || writerId == null) return false;
      const ownerId = (typeof writerId === 'object') ? writerId.id : writerId;
      return String(me) === String(ownerId);
    },
    displayAuthor(isDeleted, writerNickname) {
      if (isDeleted) return '알 수 없음';
        return writerNickname;
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
    toggleChildMenu(childId) {
      if (this.$set) this.$set(this.childMenus, childId, !this.childMenus[childId]);
      else this.childMenus[childId] = !this.childMenus[childId];
    },
    startChildEdit(child) {
      const next = prompt('내용 수정', child.content);
      if (next && next.trim()) {
        this.$emit('edit-comment', { commentId: child.id, content: next.trim() });
      }
      this.childMenus[child.id] = false;
    },
    confirmChildDelete(child) {
      this.$emit('delete-comment', child.id);
      this.childMenus[child.id] = false;
    },
  },
  mounted() {
    document.addEventListener('click', this.handleOutsideClick, true);
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleOutsideClick, true);
  },
  // Vue 2 fallback
  beforeDestroy() {
    document.removeEventListener('click', this.handleOutsideClick, true);
  },
}
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

