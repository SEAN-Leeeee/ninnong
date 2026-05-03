<template>
  <header class="header" :class="{ 'header-hidden': isHidden }">
    <div class="header-inner">
      <div class="top-row">
        <img src="/NINNong_Logo.png" alt="logo" class="header-logo" />
        <span class="slogan">여성 농구 동호인 커뮤니티</span>
        <nav class="nav">
          <router-link to="/teams" class="nav-item">팀</router-link>
          <router-link to="/community" class="nav-item">커뮤니티</router-link>
          <router-link
              v-if="user && user.teamId"
              :to="`/teams/${user.teamId}`"
              class="nav-item"
          >내팀</router-link>
        </nav>
        <div class="header-actions">
          <div class="notif-wrapper" ref="notifRef">
            <button class="notif-btn" @click="toggleNotifications" aria-label="알림">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
              </svg>
              <span v-if="totalNotifCount > 0" class="notif-badge">{{ totalNotifCount }}</span>
            </button>
            <div v-if="showNotifications" class="notif-dropdown">
              <div class="notif-title">알림</div>
              <div v-if="applications.length === 0 && !myApplicationNotif" class="notif-empty">알림이 없습니다.</div>
              <!-- 신청자: 수락/거절 결과 알림 -->
              <div v-if="myApplicationNotif" class="notif-item notif-item--response" @click="dismissMyNotif">
                <span class="notif-dot" :class="myApplicationNotif.status === 'ACCEPT' ? 'dot-accept' : 'dot-reject'"></span>
                <span class="notif-text">
                  팀 가입 신청이
                  <strong>{{ myApplicationNotif.status === 'ACCEPT' ? '수락' : '거절' }}</strong>
                  되었습니다.
                  <span v-if="myApplicationNotif.responseMsg" class="notif-response-msg">"{{ myApplicationNotif.responseMsg }}"</span>
                </span>
              </div>
              <!-- 리더: 가입 신청 알림 -->
              <div
                  v-for="(app, idx) in applications"
                  :key="idx"
                  class="notif-item"
              >
                <span class="notif-dot"></span>
                <span class="notif-text">{{ app.name }}님이 팀 가입을 신청했습니다.</span>
              </div>
            </div>
          </div>
          <button @click="logout">로그아웃</button>
        </div>
      </div>
    </div>
  </header>
</template>

<script>
import { useUserStore } from '@stores/user.js'
import api from "@/axios.js";

export default {
  data() {
    return {
      lastScrollY: 0,
      isHidden: false,
      applications: [],
      myApplicationNotif: null,
      showNotifications: false,
    };
  },

  computed: {
    isLoggedIn() {
      return localStorage.getItem('accessToken');
    },
    user() {
      const userStore = useUserStore()
      return userStore.currentUser
    },
    totalNotifCount() {
      return this.applications.length + (this.myApplicationNotif ? 1 : 0);
    },
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll);
    document.addEventListener('click', this.handleOutsideClick);
    this.checkGotApplication();
    this.checkMyApplicationResponse();
  },
  beforeUnmount() {
    window.removeEventListener('scroll', this.handleScroll);
    document.removeEventListener('click', this.handleOutsideClick);
  },
  methods: {
    handleScroll() {
      const currentY = window.scrollY;
      if (currentY > this.lastScrollY && currentY > 100) {
        this.isHidden = true;
      } else {
        this.isHidden = false;
      }
      this.lastScrollY = currentY;
    },
    toggleNotifications() {
      this.showNotifications = !this.showNotifications;
    },
    handleOutsideClick(event) {
      if (this.$refs.notifRef && !this.$refs.notifRef.contains(event.target)) {
        this.showNotifications = false;
      }
    },
    async logout() {
      const refreshToken = localStorage.getItem('refreshToken');
      try {
        if (refreshToken) {
          await api.patch('/auth/logout', { refreshToken });
        }
      } catch (e) {
        // 백엔드 실패해도 클라이언트는 로그아웃 처리
      } finally {
        localStorage.removeItem('accessToken');
        const userStore = useUserStore();
        userStore.logout();
        this.$router.push('/login');
      }
    },
    async checkGotApplication() {
      if (!this.user?.teamId) return;
      try {
        const res = await api.get(`/teamApplication/${this.user.teamId}`);
        this.applications = res.data.filter(a => a.status === 'PENDING');
      } catch (err) {
        // ignore
      }
    },
    async checkMyApplicationResponse() {
      if (!this.user) return;
      try {
        const res = await api.get('/teamApplication/mine');
        const app = res.data;
        if (app.status && app.status !== 'PENDING' && app.isResponseChecked === false) {
          this.myApplicationNotif = app;
        }
      } catch (err) {
        // ignore
      }
    },
    async dismissMyNotif() {
      try {
        await api.patch('/teamApplication/mine/check');
      } catch (err) {
        // ignore
      }
      this.myApplicationNotif = null;
      this.showNotifications = false;
    },
  }
}
</script>

<style scoped>
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notif-wrapper {
  position: relative;
}

.notif-btn {
  position: relative;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 4px 6px;
  color: #374151;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: background 0.15s;
}
.notif-btn:hover {
  background: rgba(0, 0, 0, 0.06);
}

.notif-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: #ef4444;
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  min-width: 16px;
  height: 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 3px;
  line-height: 1;
}

.notif-dropdown {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  width: 300px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  z-index: 1000;
  overflow: hidden;
}

.notif-title {
  padding: 14px 16px 12px;
  font-weight: 700;
  font-size: 14px;
  color: #111827;
  border-bottom: 1px solid #e5e7eb;
}

.notif-empty {
  padding: 24px 16px;
  text-align: center;
  color: #9ca3af;
  font-size: 14px;
}

.notif-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
  font-size: 14px;
  color: #374151;
}
.notif-item:last-child {
  border-bottom: none;
}

.notif-dot {
  flex-shrink: 0;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #3b82f6;
  margin-top: 5px;
}

.notif-text {
  line-height: 1.5;
}

.notif-item--response {
  cursor: pointer;
  background: #f8faff;
}
.notif-item--response:hover {
  background: #eff6ff;
}

.dot-accept { background: #22c55e; }
.dot-reject { background: #ef4444; }

.notif-response-msg {
  display: block;
  font-size: 12px;
  color: #6b7280;
  margin-top: 2px;
}
</style>
