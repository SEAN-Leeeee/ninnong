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
            class="team-join-button"
            :disabled="requestSent"
            @click="openRequestModal"
        >
          {{ requestSent ? '요청 대기중' : '가입 요청' }}
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
            <button class="request-submit-button" @click="submitRequest">요청</button>
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
    applicationTeamId: Number

  },
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
      return this.team.isRecruitingMembers && this.applicationTeamId === 0 && this.user.teamId ===0;
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
    async submitRequest() {
      const applicationRequest = {
        userName: this.user.name,
        draftLevel : this.user.draftLevel,
        requestMsg : this.requestMsg
      }
      try {
        await api.post('/teamApplication/{teamId}', applicationRequest);
      }  catch (err) {
        const msg = err.response?.data?.message || '오류가 발생했습니다. 관리자에게 문의하세요';
        alert(msg);
      }

      this.requestSent = true
      this.showRequestModal = false
    }
  }
}
</script>
