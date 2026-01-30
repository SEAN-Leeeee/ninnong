<template>
  <div class="post-editor-wrapper">
    <h2>{{ isEditMode ? '게시글 수정' : '게시글 쓰기' }}</h2>

    <div class="form-row">
      <select v-model="category">
        <option disabled value="">카테고리 선택</option>
        <option
            v-for="([key, label]) in categoryEntries"
            :key="key"
            :value="key"
        >
          {{ label }}
        </option>
      </select>

      <select v-if="subjectOptions.length" v-model="subject">
        <option disabled value="">게시판 선택</option>
        <option
            v-for="opt in subjectOptions"
            :key="opt.value"
            :value="opt.value"
        >
          {{ opt.label }}
        </option>
      </select>
    </div>

    <input v-model="title" type="text" placeholder="제목을 입력하세요" />

    <textarea v-model="content" placeholder="내용을 입력하세요" rows="10"></textarea>

    <div class="image-upload">
      <label class="file-label">
        + 📷
        <input
            ref="fileInput"
            type="file"
            accept="image/*"
            multiple
            @change="handleImgUpload"
            style="display: none"
            hidden />
      </label>

      <div class="image-preview">
        <div v-for="image in imageList" :key="image.id" class="preview-item">
          <img :src="image.url" />
          <button class="delete-image-btn" @click="removeImage(image.id)">X</button>
        </div>
      </div>
    </div>

    <div class="form-footer">
      <button class="btn-secondary" @click="$router.back()">{{ isEditMode ? '취소' : '닫기' }}</button>
      <button class="btn-primary" :disabled="isNotFormFilled" @click="submit">{{ isEditMode ? '수정' : '등록' }}</button>
    </div>
  </div>
</template>

<script>
import api from '@/axios.js'
import { uploadMultipleFiles } from '@/api/upload.js'

const BASE_URL = 'http://localhost:8080';

export default {
  name: 'WritePost',
  data() {
    return {
      category: '',
      subject: '',
      title: '',
      content: '',
      imageList: [], // Unified list for all images
      categoryMap: {
        FREE: '자유',
        PROMO: '홍보',
        MATCHING: '매치'
      },
      subjectMap: {
        PROMO: [
          { value: 'CONTEST', label: '대회' },
          { value: 'EVENT', label: '이벤트' }
        ],
        MATCHING: [
          { value: 'MATCH', label: '교류전' },
          { value: 'GUEST', label: '게스트' }
        ]
      },
    }
  },
  computed: {
    isEditMode() {
      return !!this.$route.params.id;
    },
    postId() {
      return this.$route.params.id;
    },
    categoryEntries() {
      return Object.entries(this.categoryMap)
    },
    subjectOptions() {
      return this.subjectMap[this.category] || []
    },
    isNotFormFilled() {
      if (this.category !== "FREE" && this.subject === "") {
        return true;
      } else return this.title === "" || this.content === "";
    },
  },
  async mounted() {
    if (this.isEditMode) {
      await this.loadPostData();
    }
  },
  methods: {
    toAbsoluteUrl(path) {
      if (!path) return path;

      if (path.startsWith('blob:') || path.startsWith('data:')) {
        return path;
      }
      const fixedPath = path.startsWith('/api/uploads/')
          ? path.replace(/^\/api/, '')
          : path;

      return /^https?:/i.test(fixedPath)
          ? fixedPath
          : `${BASE_URL}${fixedPath}`;
    },
    async loadPostData() {
      try {
        const res = await api.get(`/posts/${this.postId}`);
        const post = res.data;
        this.title = post.title;
        this.category = post.category;
        this.subject = post.subject;
        console.log(this.category)
        console.log(this.subject)
        const contentHtml = post.content || '';
        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = contentHtml;

        const loadedImages = [];
        tempDiv.querySelectorAll('img').forEach(img => {
          if (img.src) {
            loadedImages.push({
              id: Date.now() + Math.random(),
              url: this.toAbsoluteUrl(img.getAttribute('src')),
              type: 'existing',
            });
          }
        });
        this.imageList = loadedImages;

        tempDiv.querySelectorAll('img').forEach(img => img.remove());
        tempDiv.querySelectorAll('br').forEach(br => br.replaceWith('\n'));
        this.content = tempDiv.innerText.trim();

      } catch (error) {
        console.error("게시글 정보를 불러오는데 실패했습니다.", error);
        alert("게시글 정보를 불러올 수 없습니다.");
        this.$router.back();
      }
    },
    handleImgUpload(e) {
      const newFiles = Array.from(e.target.files);
      const newImages = newFiles.map(file => ({
        id: Date.now() + Math.random(),
        url: URL.createObjectURL(file),
        type: 'new',
        file: file,
      }));
      this.imageList = [...this.imageList, ...newImages];
    },
    removeImage(id) {
      this.imageList = this.imageList.filter(image => image.id !== id);
    },
    async submit() {
      if (this.isEditMode) {
        await this.updatePost();
      } else {
        await this.createPost();
      }
    },
    async createPost() {
      try {
        const filesToUpload = this.imageList.filter(img => img.type === 'new').map(img => img.file);
        const uploaded = await uploadMultipleFiles(filesToUpload);
        const imageList = uploaded.map(r => r.fileUrl);

        const imageHtml = imageList.map(url => `<img src="${this.toAbsoluteUrl(url)}" />`).join('<br/>');
        const finalContent = [this.content, imageHtml].filter(Boolean).join('<br/>');

        const res = await api.post('/posts', {
          category: this.category,
          subject: this.category == 'FREE' ? 'FREE' : this.subject,
          title: this.title,
          content: finalContent,
          imageUrls: imageList
        });
        alert('등록 완료');
        this.$router.push(`/post/${res.data.id}`);
      } catch (err) {
        console.error(err);
        alert('등록 실패');
      }
    },
    async updatePost() {
      try {
        const newFilesToUpload = this.imageList.filter(img => img.type === 'new').map(img => img.file);
        const newUploaded = await uploadMultipleFiles(newFilesToUpload);
        const newImageUrls = newUploaded.map(r => this.toAbsoluteUrl(r.fileUrl));

        const existingImageUrls = this.imageList.filter(img => img.type === 'existing').map(img => img.url);

        const allImageUrls = [...existingImageUrls, ...newImageUrls];

        const imageHtml = allImageUrls.map(url => `<img src="${url}" />`).join('<br/>');
        const finalContent = [this.content, imageHtml].filter(Boolean).join('<br/>');

        await api.patch(`/posts/${this.postId}`, {
          category: this.category,
          subject: this.category == 'FREE' ? 'FREE' : this.subject,
          title: this.title,
          content: finalContent,
          imageUrls: allImageUrls
        });
        alert('수정 완료');
        this.$router.push(`/post/${this.postId}`);
      } catch (err) {
        console.error(err);
        alert('수정 실패');
      }
    }
  },
}
</script>

<style scoped>
.form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 16px;
}

.image-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.preview-item {
  position: relative;
  width: 100px;
  height: 100px;
}

.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.delete-image-btn {
  position: absolute;
  top: 2px;
  right: 2px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  width: 20px;
  height: 20px;
  font-size: 12px;
  line-height: 20px;
  text-align: center;
  padding: 0;
}

/* Copied from CommentItem.vue for consistency */
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

.btn-primary {
  background: #2563eb;
  color: #fff;
  border-color: #2563eb;
}

.btn-secondary {
  background: #ffffff;
  color: #111827;
  border-color: #d1d5db;
}

.btn-secondary:hover {
  background: #f9fafb;
}
</style>

