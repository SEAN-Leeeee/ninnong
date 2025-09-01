<template>
  <div class="team-create-modal-overlay" @click.self="$emit('close')">
    <div class="team-create-modal">
      <h2 class="modal-title">팀 만들기</h2>

      <div class="logo-name-row">
        <div class="logo-upload-box" @click="triggerFileInput">
          <img :src="getTeamLogo(newTeamInfo)" alt="팀 로고" />
          <input
              ref="fileInput"
              type="file"
              accept="image/*"
              @change="handleLogoUpload"
              style="display: none"
          />
        </div>
        <input
            v-model="newTeamInfo.name"
            type="text"
            class="team-name-input"
            placeholder="팀 이름을 입력하세요"
        />
      </div>

      <label class="modal-label">지역 선택</label>
      <div class="region-row">
        <select v-model="selectedRegion" class="modal-input region-dropdown">
          <option disabled value="">지역 선택</option>
          <option value="custom">직접 입력</option>
          <option v-for="loc in region" :key="loc" :value="loc">
            {{ loc.name }}
          </option>
        </select>

        <input
            v-if="selectedRegion === 'custom'"
            v-model="customRegion"
            type="text"
            class="modal-input custom-region-input"
            placeholder="예시) 아산 or 경기광주"
        />
      </div>

      <label class="modal-label">회비</label>
      <input
          id="fee"
          type="text"
          :value="displayMembershipFee"
          @input="handleInputNumber"
          placeholder="회비를 입력해주세요"
          class="team-name-input"
      />

      <label class="modal-label">모임 일정</label>
      <div class="schedule-row">
        <select v-model="day" class="modal-input day-dropdown">
          <option disabled value="">요일 선택</option>
          <option v-for="day in days" :key="day" :value="day">
            {{ day }}
          </option>
        </select>

        <select v-model="time" class="modal-input time-dropdown">
          <option disabled value="">시간 선택</option>
          <option v-for="time in times" :key="time" :value="time">
            {{ time }}
          </option>
        </select>
      </div>

      <label class="modal-label">설명</label>
      <textarea
          maxlength="300"
          v-model="newTeamInfo.description"
          class="modal-textarea"
          placeholder="팀 설명을 입력해주세요 (최대 300자)"
      ></textarea>
      <p>{{ newTeamInfo.description.length }}/300자</p>

      <div class="recruiting-row">
        <span>회원 모집중</span>
        <input
            type="checkbox"
            v-model="newTeamInfo.isRecruitingMembers"
            class="recruiting-checkbox"
        />
      </div>

      <div class="modal-button-row">
        <button class="button-grey" @click="$emit('close')">취소</button>
        <button class="button-blue" @click="submit" :disabled="isUploadingLogo">
          {{ isUploadingLogo ? '업로드 중...' : '등록' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import DOMPurify from 'dompurify'
import he from 'he'

import api from '@/axios.js'
import { uploadFile } from '@/api/upload.js'
import imageCompression from 'browser-image-compression'
import { useUserStore } from '@stores/user.js'

export default {
  name: 'TeamCreateModal',
  data() {
    return {
      newTeamInfo: {
        name: '',
        logo: '',
        region: '',
        description: '',
        isRecruitingMembers: false,
        meetingDay: '',
        membershipFee: 0,
      },
      day: '월요일',
      time: '오전',
      selectedRegion: '',
      customRegion: '',
      region: [],
      days: ['월요일', '화요일', '수요일', '목요일', '금요일', '토요일', '일요일'],
      times: ['오전', '오후', '저녁'],
      isUploadingLogo: false,
    }
  },
  computed: {
    displayMembershipFee() {
      return this.newTeamInfo.membershipFee.toLocaleString()
    },
    user() {
      const userStore = useUserStore()
      return userStore.currentUser
    },
  },
  watch: {
    selectedRegion(newVal) {
      if (newVal === 'custom') {
        this.newTeamInfo.region = this.customRegion
      } else {
        this.newTeamInfo.region = newVal.name
        this.customRegion = ''
      }
    },
    customRegion(newVal) {
      console.log(newVal)

      if (this.selectedRegion === 'custom') {
        this.newTeamInfo.region = newVal
      }
    },
  },
  created() {
    this.loadRegions()
  },
  methods: {
    getTeamLogo(team) {
      if (!team.logo) return '/basic.png'
      if (/^https?:\/\//.test(team.logo)) return team.logo // 절대 URL
      return `http://localhost:8080${team.logo}` // 상대 URL → 서버 접두부
    },
    async loadRegions() {
      try {
        const res = await api.get('/regions')
        this.region = res.data
      } catch (err) {
        console.error('지역 불러오기 실패:', err)
      }
    },
    async handleLogoUpload(e) {
      const file = e?.target?.files?.[0];
      if (!file) return;

      this.isUploadingLogo = true;
      try {
        if (!this.validateImageType(file)) {
          this.resetInput(e);
          return;
        }

        const fileToUpload = await this.prepareFileForUpload(file);
        if (!fileToUpload) {
          this.resetInput(e);
          return;
        }

        const result = await uploadFile(fileToUpload);
        this.newTeamInfo.logo = result.fileUrl;
      } catch (err) {
        const msg = err?.response?.data?.message || err?.message || '오류가 발생했습니다. 관리자에게 문의하세요';
        alert(msg);
      } finally {
        this.isUploadingLogo = false;
        this.resetInput(e);
      }
    },

    validateImageType(file) {
      const ALLOWED = ['image/jpeg', 'image/png', 'image/webp'];
      const ok = ALLOWED.includes(file.type);
      if (!ok) alert('지원하지 않는 이미지 형식입니다. JPG/PNG/WEBP만 올려주세요.');
      return ok;
    },

    async prepareFileForUpload(file) {
      if (!this.needCompression(file)) return file;

      const compressed = await this.compressImage(file);
      if (!this.withinServerLimit(compressed)) {
        alert('이미지 파일이 아직 큽니다. 해상도를 더 낮추거나 다른 이미지를 선택해주세요.');
        return null;
      }
      return this.toFileFromBlob(compressed, file.name);
    },

    needCompression(file) {
      const THRESHOLD = 250 * 1024; // 250KB
      return file.size > THRESHOLD;
    },

    async compressImage(file) {
      const options = {
        maxSizeMB: 0.6,
        maxWidthOrHeight: 1280,
        initialQuality: 0.7,
        useWebWorker: true,
        fileType: file.type === 'image/png' ? 'image/jpeg' : undefined,
        alwaysKeepResolution: false,
      };
      return imageCompression(file, options);
    },

    withinServerLimit(blob) {
      const MAX_UPLOAD = 2 * 1024 * 1024; // 2MB (환경에 맞게 조절)
      return blob.size <= MAX_UPLOAD;
    },

    toFileFromBlob(blob, originalName) {
      const ext = (blob.type.split('/')[1] || 'jpeg').replace('jpeg', 'jpg');
      const base = originalName.replace(/\.[^/.]+$/, '');
      return new File([blob], `${base}-compressed.${ext}`, {
        type: blob.type,
        lastModified: Date.now(),
      });
    },

    resetInput(e) {
      if (e?.target) e.target.value = '';
    },
    triggerFileInput() {
      this.$refs.fileInput.click()
    },
    async submit() {
      this.sanitizeDescription()
      try {
        this.newTeamInfo.meetingDay = this.day + ' ' + this.time

        const res = await api.post('/teams', this.newTeamInfo)
        if (this.customRegion !== '') {
          await this.updateRegion(this.newTeamInfo.region)
        }
        this.user.teamId = res.data.id;
        alert("팀이 생성되었습니다!");
        this.$emit('create')
        this.$emit('close')
      } catch (err) {
        const msg = err.response?.data?.message || '오류가 발생했습니다. 관리자에게 문의하세요'
        alert(msg)
      }
    },
    async updateRegion(region) {
      try {
        await api.post('/regions', {
          region: region,
        })
      } catch (err) {
        const msg = err.response?.data?.message || '오류가 발생했습니다. 관리자에게 문의하세요'
        alert(msg)
      }
    },
    handleInputNumber(e) {
      const input = e.target.value
      const numeric = input.replace(/[^\d]/g, '')
      this.newTeamInfo.membershipFee = numeric ? parseInt(numeric, 10) : 0
      e.target.value = this.displayMembershipFee
    },
    sanitizeDescription() {
      const dirty = this.newTeamInfo.description

      const clean = DOMPurify.sanitize(dirty, {
        ALLOWED_TAGS: [],
        ALLOWED_ATTR: [],
      })

      this.newTeamInfo.description = he.decode(clean)
    },
  },
}
</script>
