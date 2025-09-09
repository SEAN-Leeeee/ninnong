<template>
  <div class="post-detail">
    <div class="pd-header">
      <div class="pd-title-row">
        <div class="pd-badges">
          <span class="subject-badge" :class="subjectClass(post.subject)">
                  {{ subjectLabel(post.subject) }}
                </span>
        </div>
        <h1 class="pd-title" :title="post.title">{{ post.title}}</h1>
      </div>

      <div class="pd-meta">
        <div class="pd-meta-left">
          <span class="pd-writer">{{ post.nickname}}</span>
          <span class="pd-dot">·</span>
          <time class="pd-date">{{ formatDate(post.createdAt) }}</time>
        </div>
<!--        <div class="pd-meta-right">-->
<!--          <span class="pd-meta-chip">조회 {{ num(post.views) }}</span>-->
<!--          <span class="pd-meta-chip">댓글 {{ num(post.commentCount) }}</span>-->
<!--        </div>-->
      </div>
    </div>

    <div class="pd-content" v-html="contentHtml"></div>

    <div class="pd-actions">
      <div class="pd-actions-right">
        <button class="pd-btn back-btn" @click="goBack">목록</button>
        <button class="pd-btn" @click="editPost">수정</button>
        <button class="pd-btn danger" @click="deletePost">삭제</button>
      </div>
    </div>

    <hr class="pd-sep" />

    <CommentSection
        :comments="post.comments"
        @reply-submitted="addReply"
        @comment-added="addComment"
        @comment-deleted="deleteComment"
    />
  </div>
</template>

<script>
import api from '@/axios.js'
import CommentSection from '@/views/comment/CommentSection.vue'

const BASE_URL = 'http://localhost:8080'

export default {
  name: 'PostDetail',
  components: { CommentSection },
  data() {
    return {
      post: {},
      subjectMap: {
        PROMO: [
          { value: 'CONTEST', label: '대회' },
          { value: 'EVENT',   label: '이벤트' }
        ],
        MATCHING: [
          { value: 'MATCH', label: '교류전' },
          { value: 'GUEST',      label: '게스트' }
        ],
        FREE: [{ value: 'FREE', label: '자유' }]
      },
    }
  },
  async mounted() {
    this.initPost();
  },
  computed: {
    // 콘텐츠 내부의 상대 경로(/api/uploads, /uploads 등)을 절대 URL로 변환해서 표시
    contentHtml() {
      return this.rewriteRelativeMedia(this.post.content || '')
    }
  },
  methods: {
    async initPost() {
      const id = this.$route.params.id;
      try {
        const res = await api.get(`/posts/${id}`)
        this.post = res.data
      } catch (e) {
        console.error(e)
      }
    },
    formatDate(v) {
      if (!v) return ''
      const d = new Date(v)
      return d.toLocaleString('ko-KR', {
        year: '2-digit',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    num(n) {
      return typeof n === 'number' ? n.toLocaleString('ko-KR') : n
    },
    goBack() {
      this.$router.push('/community')
    },
    editPost() {
      this.$router.push(`/edit/${this.post.id}`)
    },
    deletePost() {
      // TODO: 실제 삭제 API 연동
      alert('삭제 기능 준비 중입니다.')
    },
    addComment(comment) {
      this.post.comments.push(comment)
      this.post.commentCount++
    },
    addReply(parentId, reply) {
      const recur = (list) => {
        for (const c of list) {
          if (c.id === parentId) { c.children.push(reply); return true }
          if (c.children?.length && recur(c.children)) return true
        }
        return false
      }
      recur(this.post.comments)
      this.post.commentCount++
    },
    deleteComment(idToDelete) {
      const prune = (list) =>
          list.filter(c => {
            if (c.id === idToDelete) return false
            if (c.children?.length) c.children = prune(c.children)
            return true
          })
      this.post.comments = prune(this.post.comments)
      this.post.commentCount = Math.max(0, this.post.commentCount - 1)
    },

    subjectLabel(subj) {
      const s = (subj || '').toString();

      const upper = s.toUpperCase();
      const label = this.codeToLabel(upper);
      return label || s;
    },
    subjectClass(subj) {
      const s = (subj || '').toString().toLowerCase();
      if (s.includes('event')) return 'subject-event';
      if (s.includes('guest')) return 'subject-guest';
      if (s.includes('contest')) return 'subject-promo';
      if (s.includes('match')) return 'subject-matching';
      return 'subject-free';
    },
    codeToLabel(code) {
      for (const arr of Object.values(this.subjectMap)) {
        const found = (arr || []).find(o => o.value === code);
        if (found) return found.label;
      }
      return '';
    },
    rewriteRelativeMedia(html) {
      if (!html) return ''
      const wrapper = document.createElement('div')
      wrapper.innerHTML = html

      const toAbsolute = (path) => {
        if (!path) return path
        // /api/uploads/... -> /uploads/... 로 치환(401 방지)
        const fixed = path.startsWith('/api/uploads/')
            ? path.replace(/^\/api/, '')
            : path
        return /^https?:/i.test(fixed) || fixed.startsWith('data:')
            ? fixed
            : `${BASE_URL}${fixed}`
      }

      wrapper.querySelectorAll('img').forEach(img => {
        const src = img.getAttribute('src') || ''
        if (src) {
          img.setAttribute('src', toAbsolute(src))
          img.setAttribute('loading', 'lazy')
          img.setAttribute('decoding', 'async')
        }
      })

      wrapper.querySelectorAll('a').forEach(a => {
        const href = a.getAttribute('href') || ''
        if (href && href.startsWith('/')) {
          a.setAttribute('href', toAbsolute(href))
        }
      })

      return wrapper.innerHTML
    }
  }
}
</script>
