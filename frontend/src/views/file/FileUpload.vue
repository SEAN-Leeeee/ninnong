<!-- src/components/FileUpload.vue -->
<template>
  <div class="file-upload-container">
    <input type="file" accept="image/*" @change="handleFileChange" />

    <div v-if="previewUrl" class="preview">
      <p>업로드된 이미지:</p>
      <img :src="previewUrl" alt="미리보기" width="200" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { uploadFile } from '@/api/upload.js'

const previewUrl = ref(null)

const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    const result = await uploadFile(file)
    previewUrl.value = result.fileUrl
  } catch (err) {
    console.error('업로드 실패:', err)
  }
}
</script>

<style scoped>
.file-upload-container {
  padding: 1rem;
  border: 1px solid #ccc;
  width: 300px;
  border-radius: 8px;
}

.preview {
  margin-top: 1rem;
}
</style>
