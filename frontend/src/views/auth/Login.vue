<template>
  <div class="auth-page">
    <h1 class="headline-xl">
      The Home of Women’s Hoops. <br />Step In. <br />NINNong.
    </h1>

    <Galaxy
        class="galaxy-container"
        :focal="[0.5, 0.5]"
        :rotation="[1.0, 0.0]"
        :star-speed="0.1"
        :density="1"
        :hue-shift="140"
        :disable-animation="false"
        :speed="0.2"
        :mouse-interaction="true"
        :glow-intensity="0.2"
        :saturation="0.0"
        :mouse-repulsion="true"
        :repulsion-strength="1"
        :twinkle-intensity="0.3"
        :rotation-speed="0.1"
        :auto-center-repulsion="0"
        :transparent="true"
    />

    <form class="form login-form-box" @submit.prevent="handleLogin">
      <label for="password">로그인</label>

      <div class="form-group">
        <div class="input-wrap">
          <input id="email" v-model="email" placeholder="이메일을 입력해주세요." />
        </div>
      </div>

      <div class="form-group">
        <div class="input-wrap">
          <input id="password" type="password" v-model="password" placeholder="비밀번호를 입력해주세요." />
        </div>
      </div>

      <button :disabled="isNotFormFilled" type="submit" class="submit-btn">로그인</button>
      <router-link class="text-button" to="/register">
      회원가입
    </router-link>

    </form>
  </div>
</template>

<script>
import api from '@/axios.js';
import { useUserStore } from '@stores/user.js';
import Galaxy from "../../../component/Galaxy/Galaxy.vue"

export default {
  name: 'Login',
  components: {
    Galaxy
  },
  data() {
    return {
      email: '',
      password: '',
    };
  },
  mounted() {
    document.body.style.paddingTop = '0';
  },
  beforeUnmount() {
    document.body.style.paddingTop = '120px';
  },
  computed: {
    isNotFormFilled() {
      if(this.email == '' || this.password == '') return true;
    }
  },
  methods: {
    async handleLogin() {
      try {
        const response = await api.post('/auth/login', {
          email: this.email,
          password: this.password,
        });
        const { accessToken, refreshToken } = response.data;
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);

        const userStore = useUserStore();
        await userStore.fetchCurrentUser(this.email, this.password);

        this.$router.push('/teams');
      } catch (err) {
        const msg =
          err.response?.data?.message ||
          '오류가 발생했습니다. 관리자에게 문의하세요.';
        alert(msg);
      }
    },
  },
};
</script>

<style scoped>
.auth-page {
  padding-top: 120px;
}
</style>
