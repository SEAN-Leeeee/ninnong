
<template>
  <div class="team-board-modal-overlay" @click.self="closeModal">
    <div class="team-board-modal">

      <button class="team-board-close-btn" @click="closeModal">✕</button>

      <!-- 팀 로고 영역 -->
      <div class="team-board-logo-banner">
        <img :src="getTeamLogo(team)" alt="팀 로고" />
      </div>

      <div class="team-board-info">
        <p><strong>{{ team.name }}</strong></p>
        <p><strong>지역:</strong> {{ team.region }}</p>
        <p><strong>인원:</strong> {{ team.memberCount }}</p>
        <p><strong>모임 일정:</strong> {{ team.meetingDay }}</p>
        <p><strong>회비:</strong> {{ team.membershipFee }}</p>
        <p><strong>설명:</strong> {{ team.description }}</p>
      </div>

      <!-- 버튼 -->
      <div class="team-board-actions" v-if="showRequestBox" >
        <button
            v-show="applicationTeamId == 0"
            class="team-join-button"
            :disabled="requestSent"
            @click="openRequestModal"
        >
          가입 요청
        </button>
        <button
            v-show="applicationTeamId == team.id"
            class="team-join-button"
            @click="cancelJoinRequest"
        >
          가입 요청 취소
        </button>
      </div>

      <!-- 가입 요청 모달 -->
      <div v-if="showRequestModal" class="team-board-modal-overlay" @click.self="showRequestModal = false">
        <div class="team-board-modal">

          <h2 class="request-modal-title" >가입 요청</h2>

          <div class="request-modal-content">
            <p class="request-profile"><strong>이름:</strong> {{ user.name }}</p>
            <label for="draftLevelOptions" class="request-profile">
              <strong>선출:</strong> {{ draftLevelOptions[user.draftLevel] }}
            </label>

            <textarea
                v-model="requestMsg"
                placeholder="요청 메시지를 작성해주세요"
                class="request-textarea"
            ></textarea>
          </div>

          <div class="request-button-row">
            <button class="request-cancel-button" @click="showRequestModal = false">취소</button>
            <button class="request-submit-button" @click="submitJoinRequest">요청</button>
          </div>

        </div>
      </div>

    </div>
  </div>
</template>


<script>
import api from "@/axios.js";
import { useUserStore } from '@stores/user.js'

export default {
  name: 'SelectedTeam',
  props: {
    team: {
      type: Object,
      required: true,
    },
    applicationTeamId: Number,

  },
  emits: ['close', 'application-request'],
  data() {
    return {
      showRequestModal: false,
      requestMsg: '',
      requestSent: false,
      draftLevelOptions: {
        ELEMENTARY: '초등부',
        MIDDLE: '중등부',
        HIGH: '고등부',
        UNIVERSITY: '대학부',
        NONE: '해당없음',
      },
    }
  },
  computed: {
    user() {
      const userStore = useUserStore()
      return userStore.currentUser
    },
    showRequestBox() {
      return this.team.isRecruitingMembers && this.user.teamId ===0;
    },
  },
  methods: {
    getTeamLogo(team) {
      return team.logo ? `http://localhost:8080${team.logo}` : '/basic.png';
    },
    closeModal() {
      this.$emit('close')
    },
    openRequestModal() {
      this.showRequestModal = true
    },
    async submitJoinRequest() {
      const applicationRequest = {
        userName: this.user.name,
        draftLevel : this.user.draftLevel,
        requestMsg : this.requestMsg
      }
      try {
        const res = await api.post(`/teamApplication/${this.team.id}`, applicationRequest);
        console.log(res.data)
        this.$emit('application-request');
      }  catch (err) {
        const msg = err.response?.data?.message || '오류가 발생했습니다. 관리자에게 문의하세요';
        alert(msg);
      }

      this.requestSent = true
      this.showRequestModal = false
    },
    async cancelJoinRequest() {
      try {
        await api.delete(`/teamApplication/${this.team.id}`);
        this.$emit('application-request');

      } catch (err) {
        const msg = err.response?.data?.message || '오류가 발생했습니다. 관리자에게 문의하세요';
        alert(msg);
      }

    },
  }
}
</script>

<style scoped>
/* ========== Modal Overlay ========== */
.team-board-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif;
}

/* ========== Modal Box ========== */
.team-board-modal {
  background-color: #FFFFFF;
  border-radius: 12px;
  padding: 20px 24px;
  max-width: 460px;
  width: 90%;
  border: 1px solid #E5E7EB;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  position: relative;
}

.team-board-close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  font-size: 20px;
  color: #6B7280;
  cursor: pointer;
  padding: 4px;
  line-height: 1;
}

.team-board-close-btn:hover {
  color: #111827;
}

/* ========== Logo Banner ========== */
.team-board-logo-banner {
  width: 100%;
  height: 180px;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 20px;
}

.team-board-logo-banner img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* ========== Info ========== */
.team-board-info {
  font-size: 14px;
  color: #111827;
  line-height: 1.8;
}

.team-board-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #111827;
}

.team-board-info p strong {
  color: #111827;
  font-weight: 700;
}

.team-board-info p:first-child {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 12px;
}

/* ========== Actions ========== */
.team-board-actions {
  margin-top: 24px;
}

.team-join-button {
  width: 100%;
  background-color: #3B82F6;
  color: #FFFFFF;
  font-size: 15px;
  font-weight: 600;
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s;
}

.team-join-button:hover {
  background-color: #2563EB;
}

.team-join-button:disabled {
  background-color: #E5E7EB;
  color: #9CA3AF;
  cursor: not-allowed;
}

/* ========== Request Modal ========== */
.request-modal-title {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  margin-bottom: 16px;
}

.request-modal-content {
  text-align: left;
}

.request-profile {
  font-size: 14px;
  color: #111827;
  margin-bottom: 12px;
  display: block;
}

.request-textarea {
  width: 100%;
  height: 100px;
  padding: 10px 14px;
  font-size: 14px;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  resize: none;
  margin: 16px 0;
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif;
  outline: none;
  color: #111827;
  box-sizing: border-box;
}

.request-textarea:focus {
  border-color: #3B82F6;
}

.request-textarea::placeholder {
  color: #9CA3AF;
}

.request-button-row {
  display: flex;
  gap: 12px;
}

.request-cancel-button,
.request-submit-button {
  flex: 1;
  font-size: 15px;
  font-weight: 600;
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s;
}

.request-cancel-button {
  background-color: #FFFFFF;
  color: #111827;
  border: 1px solid #E5E7EB;
}

.request-cancel-button:hover {
  background-color: #F9FAFB;
}

.request-submit-button {
  background-color: #3B82F6;
  color: #FFFFFF;
}

.request-submit-button:hover {
  background-color: #2563EB;
}
</style>
