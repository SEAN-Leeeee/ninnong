<template>
  <div class="video-page">
    <h1 class="title">📹 팀 활동 영상</h1>

    <!-- 월 선택 & 리더만 추가 가능 -->
    <div class="video-controls">
      <button v-if="user.isLeader" @click="showUpload = !showUpload">
        {{ showUpload ? '입력 닫기' : '+ 영상 추가하기' }}
      </button>
    </div>

    <!-- 태그 필터 -->
    <div class="tag-filter" v-if="uniqueTags.length > 0">
      <button
          :class="{ active: selectedTag === null }"
          @click="selectedTag = null"
      >#전체</button>
      <button
          v-for="tag in uniqueTags"
          :key="tag"
          :class="{ active: selectedTag === tag }"
          @click="selectedTag = tag"
      >#{{ tag }}</button>
    </div>

    <!-- 업로드 폼 -->
    <div v-if="showUpload && user.isLeader" class="upload-form">
      <input v-model="uploadForm.title" type="text" placeholder="영상 제목" />
      <input v-model="uploadForm.url" type="text" placeholder="YouTube 링크 또는 mp4 경로" />
      <input v-model="uploadForm.tags" type="text" placeholder="태그 (예: 연습, 하이라이트)" />
      <button @click="addVideo">저장</button>
    </div>

    <!-- 영상 리스트 -->
    <div v-if="filteredVideos.length > 0" class="video-list">
      <div v-for="video in filteredVideos" :key="video.id" class="video-card">
        <div class="thumbnail">
          <iframe
              v-if="isYouTube(video.url)"
              :src="embedYouTube(video.url)"
              frameborder="0"
              allowfullscreen
          ></iframe>
          <video v-else controls>
            <source :src="video.url" type="video/mp4" />
          </video>
        </div>
        <div class="meta">
          <h3>{{ video.title }}</h3>
          <small>{{ formatDate(video.createdAt) }}</small>
          <p class="uploader">업로더: {{ video.uploader }}</p>
          <div class="tags">
            <span class="tag" v-for="tag in video.tags" :key="tag">#{{ tag }}</span>
          </div>
          <button class="delete" v-if="user.isLeader" @click="deleteVideo(video.id)">🗑 삭제</button>
        </div>
      </div>
    </div>

    <div v-else class="empty">📦 아직 업로드된 영상이 없습니다.</div>
  </div>
</template>

<script>
export default {
  name: 'VideoBoardView',
  data() {
    return {
      selectedTag: null,
      showUpload: false,
      user: {
        name: '김코딩',
        isLeader: true // ✅ 실제 서비스에선 서버에서 받아오는 정보로 대체
      },
      uploadForm: {
        title: '',
        url: '',
        tags: ''
      },
      videos: []
    };
  },
  computed: {
    filteredVideos() {
      return this.videos.filter(v => {
        const tagMatch =
            !this.selectedTag || (v.tags && v.tags.includes(this.selectedTag));
        return tagMatch; // ✅ 월 조건 제거
      });
    },
    uniqueTags() {
      const tagSet = new Set();
      this.videos.forEach(v => {
        if (Array.isArray(v.tags)) {
          v.tags.forEach(tag => tagSet.add(tag));
        }
      });
      return Array.from(tagSet);
    }
  },
  methods: {
    addVideo() {
      if (!this.uploadForm.title || !this.uploadForm.url) {
        alert('제목과 링크를 모두 입력해주세요.');
        return;
      }

      const now = new Date();
      const newVideo = {
        id: Date.now(),
        title: this.uploadForm.title,
        url: this.uploadForm.url,
        uploader: this.user.name,
        createdAt: now,
        tags: this.uploadForm.tags
            ? this.uploadForm.tags.split(',').map(t => t.trim())
            : []
      };

      this.videos.unshift(newVideo);
      this.uploadForm = { title: '', url: '', tags: '' };
      this.showUpload = false;
    },
    deleteVideo(id) {
      this.videos = this.videos.filter(v => v.id !== id);
    },
    formatDate(date) {
      const d = new Date(date);
      return `${d.getFullYear()}.${d.getMonth() + 1}.${d.getDate()}`;
    },
    isYouTube(url) {
      return url.includes('youtube.com') || url.includes('youtu.be');
    },
    embedYouTube(url) {
      let videoId = '';
      if (url.includes('youtu.be')) {
        videoId = url.split('/').pop();
      } else if (url.includes('youtube.com')) {
        const urlParams = new URL(url).searchParams;
        videoId = urlParams.get('v');
      }
      return `https://www.youtube.com/embed/${videoId}`;
    }
  }
};
</script>
