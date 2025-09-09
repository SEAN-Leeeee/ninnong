<template>
  <div class="team-section">

    <div class="team-list-header">
      <ShinyText
          text="팀 목록"
          :disabled="false"
          :speed="3"
      />
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
import ShinyText from "../../../component/ShinyText/ShinyText.vue"


import api from "@/axios.js";

export default {
  name: "TeamList",
  components: {
    SelectedTeam,
    TeamCreateModal,
    ShinyText
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
.team-card.create-card {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: 2px dashed #ccc;
  background-color: #f9f9f9;
}

.team-card.create-card:hover {
  background-color: #f1f1f1;
}

.team-card-content.create-content {
  text-align: center;
  font-size: 1.2em;
  font-weight: bold;
  color: #555;
}
</style>
