<template>
  <div class="post-form">
    <h2>게시글 쓰기</h2>

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
        <img v-for="(img, index) in previewImages" :src="img" :key="index" />
      </div>
    </div>

    <div class="form-footer">
      <button :disabled="isNotFormFilled" @click="submit">등록</button>
      <button class="cancel" @click="$router.back()">닫기</button>
    </div>
  </div>
</template>

<script>
import api from '@/axios.js'
import { uploadMultipleFiles } from '@/api/upload.js'

export default {
  name: 'WritePost',
  data() {
    return {
      category     : '',
      subject       : '',
      title         : '',
      content       : '',
      files         : [],
      previewImages : [],
      categoryMap   : {
        FREE: '자유',
        PROMO: '홍보',
        MATCHING: '매치'
      },
      subjectMap: {
        FREE: [
          {value: 'FREE', label: '자유'}
        ],
        PROMO: [
          { value: 'CONTEST', label: '대회' },
          { value: 'EVENT',   label: '이벤트' }
        ],
        MATCHING: [
          { value: 'MATCH', label: '교류전' },
          { value: 'GUEST',      label: '게스트' }
        ]
      },
    }
  },
  computed: {
    categoryEntries() {
      return Object.entries(this.categoryMap)
    },
    subjectOptions() {
      return this.subjectMap[this.category] || []
    },
    isNotFormFilled() {
      if (this.category !== "FREE" && this.subject ==="") {
        return true;
      } else if (this.title === "" || this.content === "") {
        return true;
      } else {
        return false;
      }
    },
  },
  methods: {
    handleImgUpload(e) {
      this.files = Array.from(e.target.files)
      this.previewImages = this.files.map(f => URL.createObjectURL(f))
    },
    async submit() {
      try {
        const uploaded = await uploadMultipleFiles(this.files)
        const imageList = uploaded.map(r => r.fileUrl)

        const base = api.defaults.baseURL?.replace(/\/+$/,'') || 'http://localhost:8080'
        const imageHtml = imageList.map(url => `<img src="${base}${url}" />`).join('<br/>')
        const finalContent = [this.content, imageHtml].filter(Boolean).join('<br/>')

        const res = await api.post('/posts', {
          category: this.category,
          subject: this.subject,
          title: this.title,
          content: finalContent,
          imageUrls: imageList
        })
        alert('등록 완료')
        this.$router.push(`/post/${res.data.id}`)
      } catch (err) {
        console.error(err)
        alert('등록 실패')
      }
    }
  },
  watch: {
    category() { this.subject = '' }
  }
}
</script>

