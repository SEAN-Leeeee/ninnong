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
    AttendancePanel,
    VideoPanel,
    MemberManage,
    FeePanel
  },
  data() {
    return {
      team: {},
      memberList:[],
      selectedTab: 'member',
      tabs: [
        { label: '멤버', value: 'member' },
        { label: '활동', value: 'activities' },
        { label: '영상', value: 'video' },
        { label: '회비', value: 'fee' }
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
.myteam-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.team-header-box {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.team-header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.team-logo {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #eee;
}

.team-text-info {
  display: flex;
  flex-direction: column;
}

.team-name {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.team-member-count {
  font-size: 14px;
  color: #555;
  margin: 4px 0;
}

.team-description {
  font-size: 14px;
  color: #777;
  margin-top: 8px;
  max-width: 400px;
}

.team-header-right {
  display: flex;
  align-items: center;
}

.team-map-box {
  width: 300px;
  height: 120px;
  background: #f5f5f5;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #999;
}

.team-content-box {
  display: flex;
  gap: 20px;
}

.community-sidebar {
    width: 190px;
    min-width: 190px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
}

.button {
    background: none;
    border: none;
    text-align: left;
    font-size: 17px;
    font-weight: 700;
    padding: 10px 18px;
    width: 100%;
    color: #94a3b8;
    cursor: pointer;
    border-radius: 10px;
    transition: background 0.2s, color 0.2s;
    margin-bottom: 2px;
}

.button:hover,
.button.active {
    background: #f1f5f9;
    color: #2563eb;
    font-weight: 700;
}

.team-panel {
  flex: 1;
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}
</style>