<template>
  <div class="myteam-container">
    <!-- 상단 팀 정보 -->
    <section class="team-header-box">
      <div class="team-header-left">
        <img :src="getTeamLogo(team)" alt="팀 로고" class="team-logo" />
        <div class="team-text-info">
          <h2 class="team-name">{{ team.name || '팀 이름' }}</h2>
          <p class="team-member-count">인원: {{ team.memberCount }}명</p>
          <div class="team-description">
            {{ team.description || '팀 소개가 아직 작성되지 않았습니다.' }}
          </div>
        </div>
      </div>
      <div class="team-header-right">
        <div class="team-map-box">
          정기 운동 일정 /
          지도 영역 (예정)
        </div>
      </div>
    </section>

    <!-- 하단 구조: 좌측 탭 + 우측 콘텐츠 -->
    <section class="team-content-box">
      <aside class="community-sidebar">
        <button
            class="button"
            :class="{ active: selectedTab === tab.value }"
            v-for="tab in tabs"
            :key="tab.value"
            @click="selectedTab = tab.value"
        >
          {{ tab.label }}
        </button>
      </aside>

      <main class="team-panel">
        <MemberManage
            v-if="selectedTab === 'member'"
            :memberList="memberList"
            :leader="leader"
            :user-id="user.id"
            :is-leader="isLeader"
            @save-members="handleSaveMembers"
        />
        <AttendancePanel v-if="selectedTab === 'activities'" />
        <VideoPanel v-if="selectedTab === 'video'" />
        <FeePanel v-if="selectedTab === 'fee'" />
      </main>
    </section>
  </div>
</template>
<script>
import api from "@/axios.js";
import { useUserStore } from '@stores/user.js'
import { watch } from 'vue';

import AttendancePanel from './Activities.vue'
import VideoPanel from './VideoPanel.vue'
import MemberManage from './Members.vue'
import FeePanel from './FeePanel.vue'
export default {
  name: 'MyTeam',
  components: {
    MemberManage,
    AttendancePanel,
    VideoPanel,
    FeePanel
  },
  data() {
    return {
      team: {},
      memberList:[],
      selectedTab: 'member',
      tabs: [
        { label: '멤버', value: 'member' },
        // { label: '활동', value: 'activities' },
        // { label: '영상', value: 'video' },
        // { label: '회비', value: 'fee' }
      ],
      leader: {},
      isLeader: false,
    };
  },
  mounted() {
    watch(
        () => this.user,
        (newUser) => {
          if (newUser && newUser.teamId) {
            this.getTeamInfo(newUser.teamId);
            this.getMemberList(newUser.teamId);
          }
        },
        { immediate: true },

    );
  },
  computed: {
    user() {
      const userStore = useUserStore();
      return userStore.currentUser;
    },
  },
  methods: {
    getTeamLogo(team) {
      return team.logo ? `http://localhost:8080${team.logo}` : '/basic.png';
    },
    async getMemberList(teamId) {
      const res = await api.get(`/members/${teamId}`);
      this.memberList = res.data;

      this.findLeader();
    },
    async getTeamInfo(teamId) {
      const res = await api.get(`/teams/${teamId}`);
      this.team = {
        ...res.data,
        memberCount: res.data.memberCount,
        description: res.data.description,
      };
    },
    findLeader() {
      this.leader = this.memberList.find(member => member.role === 'LEADER');
      this.isLeader = this.leader.userId === this.user.id;
    },
    async handleSaveMembers(updatedList) {
      await api.patch(`/members/${this.team.id}`, updatedList);
      this.memberList = updatedList;
    }
  },
};
</script>


<style scoped>
/* ========== Container ========== */
.myteam-container {
  padding: 0px 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, sans-serif;
  max-width: 1200px;
  margin: 0 auto;
  margin-bottom: 10px;
}

/* ========== Team Header ========== */
.team-header-box {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background: #FFFFFF;
  padding: 20px 84px;
  margin: 0;
  margin-top: 16px;
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.team-header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.team-logo {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  object-fit: cover;
  border: 1px solid #E5E7EB;
}

.team-text-info {
  display: flex;
  flex-direction: column;
}

.team-name {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.team-member-count {
  font-size: 13px;
  color: #6B7280;
  margin: 4px 0;
  font-weight: 400;
}

.team-description {
  font-size: 14px;
  color: #6B7280;
  margin-top: 8px;
  max-width: 400px;
  font-weight: 400;
}

.team-header-right {
  display: flex;
  align-items: center;
}

.team-map-box {
  width: 300px;
  height: 120px;
  background: #F9FAFB;
  border-radius: 8px;
  border: 1px solid #E5E7EB;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #6B7280;
  font-size: 13px;
}

/* ========== Content Area ========== */
.team-content-box {
  display: flex;
  gap: 24px;
}

/* ========== Sidebar ========== */
.community-sidebar {
  width: 190px;
  min-width: 190px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.button {
  background: none;
  border: none;
  text-align: left;
  font-size: 15px;
  font-weight: 600;
  padding: 10px 16px;
  width: 100%;
  color: #6B7280;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s, color 0.2s;
}

.button:hover {
  background: #EFF6FF;
  color: #111827;
}

.button.active {
  background: #EFF6FF;
  color: #3B82F6;
  font-weight: 700;
}

/* ========== Main Panel ========== */
.team-panel {
  flex: 1;
  background: #FFFFFF;
  padding: 20px 24px;
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}
</style>
