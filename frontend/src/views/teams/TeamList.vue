<template>
  <div class="team-section">

    <div class="team-list-header">
      <h2 class="team-list-title">팀 목록</h2>
      <div class="filter-row">
        <select v-model="selectedLocation">
          <option value="">전체 지역</option>
          <option v-for="loc in locations" :key="loc" :value="loc">{{ loc }}</option>
        </select>

        <button
            :class="{ active: filterRecruiting }"
            @click="toggleRecruitingFilter"
        >
          회원 모집중
        </button>
      </div>
    </div>

    <div class="team-card-container">
      <div
          class="team-card create-card"
          v-if="isNotInTeam"
          @click="showCreateModal = true"
      >
        <div class="team-card-content create-content">
          <p>+ 팀 만들기</p>
        </div>
      </div>
      <div
          class="team-card"
          v-for="team in filteredTeams"
          :key="team.id"
          @click="openTeamModal(team)"
      >
        <img :src="getTeamLogo(team)" alt="team image" />
        <div class="team-card-content">
          <h3>{{ team.name }}</h3>
          <p>지역 {{ team.region }}</p>
          <p>모임 일정 {{ team.meetingDay }}</p>
          <p>회비 {{ team.membershipFee.toLocaleString() }}</p>
          <p v-if="team.isRecruitingMembers">회원 모집중</p>
        </div>
      </div>

      <div
          class="team-card empty-card"
          v-for="n in emptyCardCount"
          :key="'empty-' + n"
      >
        <img src="@/assets/image/코트.png" alt="빈 카드" />
      </div>
    </div>

    <SelectedTeam
        v-if="showModal && selectedTeam"
        :team="selectedTeam"
        @close="showModal = false"
        :application-team-id="myApplication.teamId"
        @application-request="checkMyApplication"
    />

    <TeamCreateModal
        v-if="showCreateModal"
        @close="showCreateModal = false"
        @create="loadTeams"
    />

  </div>
</template>

<script>
import SelectedTeam from "@/views/teams/SelectedTeam.vue";
import TeamCreateModal from "@/views/teams/TeamCreateModal.vue";
import { useUserStore } from '@stores/user.js'
import api from "@/axios.js";

export default {
  name: "TeamList",
  components: {
    SelectedTeam,
    TeamCreateModal
  },
  data() {
    return {
      selectedLocation: "",
      filterRecruiting: false,
      showModal: false,
      selectedTeam: {},
      showCreateModal: false,
      teams: [],
      myApplication: {}
    };
  },
  mounted() {
    this.loadTeams();
    this.checkMyApplication();
  },
  computed: {
    isNotInTeam() {
      return (this.user?.teamId ?? 0) === 0 && this.myApplication.teamId === 0;
    },
    user() {
      const userStore = useUserStore()
      return userStore.currentUser
    },
    filteredTeams() {
      return this.teams.filter((team) => {
        const locationMatch =
            this.selectedLocation === "" ||
            team.region === this.selectedLocation;
        const recruitingMatch =
            !this.filterRecruiting || team.isRecruitingMembers;
        return locationMatch && recruitingMatch;
      });
    },
    emptyCardCount() {
      const totalCards = (this.isNotInTeam ? 1 : 0) + this.filteredTeams.length;
      const minCards = totalCards <= 8 ? 8 : Math.ceil(totalCards / 4) * 4;
      return minCards - totalCards;
    },
    locations() {
      let regions = this.teams.filter(team => team.region !== "")
          .map((team) => { return team.region;});
      return [...new Set(regions)];
    },
  },
  methods: {
    async checkMyApplication() {
      try {
        const res = await api.get('/teamApplication/mine');
        this.myApplication = res.data;
        console.log("myApplication");
        console.log(res.data);
      }  catch (err) {
        const msg = err.response?.data?.message || '오류가 발생했습니다. 관리자에게 문의하세요';
        alert(msg);
      }
    },
    getTeamLogo(team) {
      return team.logo ? `http://localhost:8080${team.logo}` : '/basic.png';
    },
    async loadTeams() {
      try {
        const res = await api.get('/teams');
        this.teams = res.data;

      } catch (err) {
        console.error("팀 목록 요청 실패:", err.response?.data || err.message);
      }
    },
    toggleRecruitingFilter() {
      this.filterRecruiting = !this.filterRecruiting;
    },
    openTeamModal(team) {
      this.selectedTeam = team;
      this.showModal = true;
    },
  },
};
</script>

<style scoped>
/* ========== TeamList Section ========== */
.team-section {
  max-width: 1200px;
  margin: 0 auto;
  margin-top: 30px;
  padding: 16px 24px;
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif;
}

.team-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.team-list-title {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

/* ========== Filters ========== */
.filter-row {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: nowrap;
}

.filter-row select,
.filter-row button {
  height: 40px;
  line-height: 38px;
  padding: 0 14px;
  margin: 0;
  font-size: 14px;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  background-color: #FFFFFF;
  color: #111827;
  outline: none;
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif;
  box-sizing: border-box;
  cursor: pointer;
  transition: all 0.2s;
  vertical-align: middle;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
}

.filter-row select {
  padding-right: 32px;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 24 24' fill='none' stroke='%236B7280' stroke-width='2'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 10px center;
}

.filter-row button {
  color: #6B7280;
  font-weight: 400;
}

.filter-row select:focus {
  border-color: #3B82F6;
}

.filter-row button:hover {
  background-color: #F9FAFB;
}

.filter-row button.active {
  background-color: #3B82F6;
  color: #FFFFFF;
  border-color: #3B82F6;
  font-weight: 600;
}

/* ========== Team Cards ========== */
.team-card-container {
  display: grid;
  grid-template-columns: repeat(4, 260px);
  gap: 24px;
  padding: 0;
  justify-content: center;
}

.team-card {
  background-color: #FFFFFF;
  border: 1px solid #E5E7EB;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  max-width: 260px;
  width: 100%;
}

.team-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.team-card img {
  width: 100%;
  height: 144px;
  object-fit: cover;
  background-color: #F9FAFB;
  display: block;
}

.team-card-content {
  padding: 12px 16px;
}

.team-card-content h3 {
  font-size: 15px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 6px 0;
}

.team-card-content p {
  font-size: 12px;
  color: #6B7280;
  margin: 2px 0;
  font-weight: 400;
}

/* ========== Create Card ========== */
.team-card.create-card {
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px dashed #E5E7EB;
  background-color: #F9FAFB;
  min-height: 208px;
  box-shadow: none;
}

.team-card.create-card:hover {
  background-color: #EFF6FF;
  border-color: #3B82F6;
}

.team-card-content.create-content {
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  color: #6B7280;
}

/* ========== Empty Card ========== */
.team-card.empty-card {
  background-color: #F9FAFB;
  border: 1px solid #E5E7EB;
  border-radius: 12px;
  box-shadow: none;
  cursor: default;
  overflow: hidden;
  height: 300px;
}

.team-card.empty-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.5;
}

.team-card.empty-card:hover {
  box-shadow: none;
}
</style>
