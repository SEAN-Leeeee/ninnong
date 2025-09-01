<template>
  <div class="post-detail">
    <!-- 헤더 -->
    <div class="pd-header">
      <div class="pd-title-row">
        <div class="pd-badges">
          <span v-if="post.category1" class="pd-badge"># {{ post.category1 }}</span>
          <span v-if="post.category2" class="pd-badge alt"># {{ post.category2 }}</span>
          <span v-if="post.subject" class="pd-badge ghost"># {{ post.subject }}</span>
        </div>
        <h1 class="pd-title" :title="post.title">{{ post.title || '제목 없음' }}</h1>
      </div>

      <div class="pd-meta">
        <div class="pd-meta-left">
          <span class="pd-writer">{{ post.nickname || '익명' }}</span>
          <span class="pd-dot">·</span>
          <time class="pd-date">{{ formatDate(post.createdAt) }}</time>
        </div>
        <div class="pd-meta-right">
          <span class="pd-meta-chip">조회 {{ num(post.views) }}</span>
          <span class="pd-meta-chip">댓글 {{ num(post.commentCount) }}</span>
        </div>
      </div>
    </div>

    <!-- 본문 -->
    <div class="pd-content" v-html="contentHtml"></div>

    <!-- 액션 -->
    <div class="pd-actions">
      <button class="pd-btn ghost" @click="goBack">목록</button>
      <div class="pd-actions-right">
        <button class="pd-btn" @click="editPost">수정</button>
        <button class="pd-btn danger" @click="deletePost">삭제</button>
      </div>
    </div>

    <hr class="pd-sep" />

    <!-- 댓글 -->
    <CommentSection
        :comments="post.comments"
        :currentUser="currentUser"
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
      currentUser: '이셔니',
      post: {
        id          : null,
        title       : '',
        writer      : '',
        nickname    : '',
        subject     : '',
        content     : '',
        category1   : '',
        category2   : '',
        createdAt   : '',
        views       : 0,
        commentCount: 0,
        comments    : []
      }
    }
  },
  async mounted() {
    const id = this.$route.params.id
    try {
      // 실제 API 형식에 맞춰 필드 매핑
      const { data } = await api.get(`/posts/${id}`)
      this.post = {
        id: data.id,
        title: data.title,
        writer: data.writer,
        nickname: data.nickname,
        subject: data.subject,
        content: data.content,
        category1: data.category,
        createdAt: data.createdAt,
        // views: data.views ?? 0,
        // commentCount: data.commentCount ?? (data.comments?.length || 0),
        // comments: data.comments ?? []
      }
      console.log(data)
    } catch (e) {
      console.error(e)
    }
  },
  computed: {
    // 콘텐츠 내부의 상대 경로(/api/uploads, /uploads 등)을 절대 URL로 변환해서 표시
    contentHtml() {
      return this.rewriteRelativeMedia(this.post.content || '')
    }
  },
  methods: {
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

    // ===== URL 보정: 여기만 추가/수정 =====
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
    // ===== URL 보정 끝 =====
  }
}
</script>
