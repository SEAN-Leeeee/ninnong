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
        <!--        <div class="actions">-->
        <!--          <input ty pe="text" placeholder="검색어를 입력하세요" class="search" />-->
        <button @click="logout">로그아웃</button>
        <!--        </div>-->
      </div>
    </div>
  </header>
</template>

<script>
import { useUserStore } from '@stores/user.js'


export default {
  data() {
    return {
      lastScrollY: 0,
      isHidden: false,
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
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll);
  },
  beforeUnmount() {
    window.removeEventListener('scroll', this.handleScroll);
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
    async logout() {
      localStorage.removeItem('accessToken');
      const userStore = useUserStore();
      await userStore.logout();
      this.$router.push('/login');
    }
  }
}
</script>
