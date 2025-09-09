<template>
  <div class="community-wrapper">
    <aside class="community-sidebar">
      <button
          class="button"
          :class="{ active: selectedCategory === key }"
          v-for="(label, key) in categories"
          :key="key"
          :value="key"
          @click="selectCategory(key)"
      >
        {{ label }}
      </button>
    </aside>

    <main class="community-main">
      <div class="main-content-container">
        <div class="top-toolbar">
          <div class="search-box search-box--compact">
            <input
                class="keyword-input"
                type="text"
                v-model.trim="searchKeyword"
                placeholder="검색어를 입력해주세요!"
                @input="onSearch"
            />
          </div>

          <div class="status-tabs subject-tabs" v-if="showSubjectTabs">
            <button
                class="tab"
                :class="{ active: selectedSubject === 'ALL' }"
                @click="setSubject('ALL')"
            >전체</button>
            <button
                v-for="opt in currentSubjectTabs"
                :key="opt.value"
                class="tab"
                :class="{ active: selectedSubject === opt.value }"
                @click="setSubject(opt.value)"
            >{{ opt.label }}</button>
          </div>

          <div class="toolbar-spacer"></div>

          <router-link class="write-button" to="/community/write">글쓰기</router-link>
        </div>
        <div class="community-board">
          <div class="post-scroll-area">
            <div class="list-header c-row sticky-header">
              <div class="col-no  col-center">번호</div>
              <div class="col-subject col-center">말머리</div>
              <div class="col-title col-start">제목</div>
              <div class="col-nick  col-center">작성자</div>
              <div class="col-date  col-center">날짜</div>
              <div class="col-views col-center">조회</div>
            </div>

            <div
                v-if="sortedPosts.length"
                v-for="post in sortedPosts"
                :key="post.id"
                :class="['c-row','c-item', rowClass(post)]"
                @click="goToPost(post.id)"
            >
              <div class="c-no col-center">{{ post.id }}</div>
              <div class="c-subject col-center">
                <span class="subject-badge" :class="subjectClass(post.subject)">
                  {{ subjectLabel(post.subject) }}
                </span>
              </div>
              <div class="c-title col-start" :title="post.title">{{ post.title }}</div>
              <div class="c-nick col-center">{{ post.nickname }}</div>
              <div class="c-date col-center">{{ formatDate(post.createdAt) }}</div>
              <div class="c-views col-center">{{ getViews(post) }}</div>
            </div>

            <div v-else class="no-post-message">
              {{ categories[selectedCategory] }} 게시판에 아직 글이 없습니다.
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import api from '@/axios.js'

export default {
  data() {
    return {
      categories: {
        ALL: '전체',
        FREE: '자유',
        PROMO: '홍보',
        MATCHING: '매치'
      },
      selectedCategory: 'ALL',
      selectedSubject: 'ALL',
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
      searchKeyword: '',
      selectedSort: 'LATEST',
      postList: []
    };
  },
  mounted() {
    this.initPostList();
  },
  computed: {
    filteredPosts() {
      let list = (this.selectedCategory === 'ALL')
          ? this.postList
          : this.postList.filter(p => p.category === this.selectedCategory);

      if (this.showSubjectTabs) {
        list = list.filter(p => this.matchesSubject(p));
      }

      if (this.searchKeyword) {
        const kw = this.searchKeyword.toLowerCase();
        list = list.filter(p => {
          const title = (p.title || '').toLowerCase();
          const nick  = (p.nickName || '').toLowerCase();
          return title.includes(kw) || nick.includes(kw);
        });
      }

      return list;
    },
    sortedPosts() {
      const list = [...this.filteredPosts];
      if (this.selectedSort === 'COMMENTS') {
        return list.sort((a,b) => (b.commentCount ?? b.comments ?? 0) - (a.commentCount ?? a.comments ?? 0));
      }
      return list.sort((a,b) => new Date(b.createdAt) - new Date(a.createdAt));
    },
    currentSubjectTabs() {
      return this.subjectMap[this.selectedCategory] || [];
    },
    showSubjectTabs() {
      return this.selectedCategory !== 'ALL' && this.selectedCategory !== 'FREE' && (this.currentSubjectTabs?.length > 0);
    }
  },
  methods: {
    async initPostList() {
      try {
        const res = await api.get('/posts');
        this.postList = res.data;
      } catch (err) {
        console.error('게시글 목록 불러오기 실패', err);
      }
    },
    selectCategory(cat) {
      this.selectedCategory = cat;
      const hasTabs = cat !== 'ALL' && cat !== 'FREE' && (this.subjectMap[cat]?.length > 0);
      if (!hasTabs) this.selectedSubject = 'ALL';
    },
    goToPost(id) {
      this.$router.push(`/post/${id}`);
    },
    formatDate(v) {
      if (!v) return '';
      const d = new Date(v);
      return d.toLocaleString('ko-KR', {
        year: '2-digit',
        month: '2-digit',
        day: '2-digit',
      });
    },
    rowClass(post) {
      return this.isNotice(post) ? 'notice' : '';
    },
    isNotice(post) {
      const s = (post.subject || '').toString();
      return s.includes('공지');
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
    getViews(post) {
      const v = post?.views ?? post?.viewCount ?? post?.hit ?? 0;
      return typeof v === 'number' ? v : parseInt(v, 10) || 0;
    },
    setSubject(code) {
      this.selectedSubject = code;
    },
    codeToLabel(code) {
      for (const arr of Object.values(this.subjectMap)) {
        const found = (arr || []).find(o => o.value === code);
        if (found) return found.label;
      }
      return '';
    },
    matchesSubject(post) {
      if (this.selectedSubject === 'ALL') return true;

      const raw = (post.subject || post.tag || '').toString();
      const upper = raw.toUpperCase();

      const code = this.selectedSubject;
      const label = this.codeToLabel(code);

      return upper === code
          || raw === label
          || upper.includes(code)
          || (label && raw.includes(label));
    },
    setSort(kind) {
      this.selectedSort = kind;
    },
  }
};
</script>

<style scoped>
.no-post-message {
  text-align: center;
  padding: 20px;
  color: #888;
}
.top-toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
}
.top-toolbar .search-box--compact {
  flex: 0 0 20%;
  max-width: 20%;
}
.top-toolbar .category-select select {
  height: 36px;
  padding: 0 10px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #fff;
}
.top-toolbar .toolbar-spacer { flex: 1 1 auto; }
.top-toolbar .sort-tabs { display: inline-flex; gap: 8px; }
.top-toolbar .sort-tabs .tab,
.top-toolbar .status-tabs .tab {
  height: 36px;
  padding: 0 14px;
  border: 1px solid #e5e7eb;
  border-radius: 999px;
  background: #f8fafc;
  font-size: 13px;
  font-weight: 600;
}
.top-toolbar .sort-tabs .tab.active,
.top-toolbar .status-tabs .tab.active {
  background: #2563eb;
  border-color: #2563eb;
  color: #fff;
}
</style>
