<template>
  <div class="post-detail">
    <div class="pd-header">
      <div class="pd-title-row">
        <div class="pd-badges">
          <span class="subject-badge" :class="subjectClass(post.subject)">
            {{ subjectLabel(post.subject) }}
          </span>
        </div>
        <h1 class="pd-title" :title="post.title">{{ post.title }}</h1>
      </div>

      <div class="pd-meta">
        <div class="pd-meta-left">
          <span class="pd-writer">{{ post.nickname }}</span>
          <span class="pd-dot">·</span>
          <time class="pd-date">{{ formatDate(post.createdAt) }}</time>
        </div>
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

    <CommentSection :post-id="postId" />

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
          { value: 'EVENT', label: '이벤트' }
        ],
        MATCHING: [
          { value: 'MATCH', label: '교류전' },
          { value: 'GUEST', label: '게스트' }
        ],
        FREE: [{ value: 'FREE', label: '자유' }]
      },
    }
  },
  async mounted() {
    await this.initPost();
  },
  computed: {
    contentHtml() {
      return this.rewriteRelativeMedia(this.post.content || '')
    },
    postId() {
      // Ensure postId is a Number
      return Number(this.$route.params.id);
    }
  },
  methods: {
    async initPost() {
      try {
        const res = await api.get(`/posts/${this.postId}`)
        this.post = res.data
      } catch (e) {
        console.error('게시글을 불러오는 중 오류가 발생했습니다.', e)
        alert('게시글을 불러올 수 없습니다.')
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
    goBack() {
      this.$router.push('/community')
    },
    editPost() {
      this.$router.push(`/edit/${this.post.id}`)
    },
    async deletePost() {
      if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) return;
      try {
        await api.delete(`/posts/${this.post.id}`);
        alert('게시글이 삭제되었습니다.');
        this.$router.push('/community');
      } catch (e) {
        console.error('게시글 삭제 중 오류가 발생했습니다.', e);
        alert('게시글을 삭제할 수 없습니다.');
      }
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
