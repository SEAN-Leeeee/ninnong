<template>
  <div class="auth-page">
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
    <form class="form" @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="name">이름</label>
        <div class="input-wrap">
          <input id="name" v-model="form.name" placeholder="이름을 입력해주세요." />
          <span v-if="errors.name" class="error">{{ errors.name }}</span>
        </div>
      </div>

      <div class="form-group">
        <label for="email">이메일</label>
        <div class="input-wrap email-wrap">
          <input
              id="email"
              v-model="form.email"
              placeholder="이메일을 입력해주세요."
              :disabled="emailVerified"
          />
          <button
              type="button"
              class="verify-btn"
              @click="sendCode"
              :disabled="!form.email || codeSending || emailVerified"
          >
            {{ emailVerified ? '인증완료 ✓' : codeSent ? '재발송' : '인증코드 발송' }}
          </button>
        </div>
        <span v-if="errors.email" class="error">{{ errors.email }}</span>
      </div>

      <!-- 인증 코드 입력 (발송 후 표시) -->
      <div class="form-group" v-if="codeSent && !emailVerified">
        <label for="verifyCode">인증 코드</label>
        <div class="input-wrap email-wrap">
          <input
              id="verifyCode"
              v-model="verifyCode"
              placeholder="이메일로 받은 6자리 코드를 입력해주세요."
              maxlength="6"
          />
          <button
              type="button"
              class="verify-btn"
              @click="verifyEmail"
              :disabled="!verifyCode || verifying"
          >
            {{ verifying ? '확인중...' : '확인' }}
          </button>
        </div>
        <span v-if="errors.verifyCode" class="error">{{ errors.verifyCode }}</span>
        <span class="hint">인증 코드는 5분간 유효합니다.</span>
      </div>

      <div class="form-group">
        <label for="nickname">닉네임</label>
        <div class="input-wrap">
          <input id="nickname" v-model="form.nickname" placeholder="닉네임을 입력해주세요." />
          <span v-if="errors.nickname" class="error">{{ errors.nickname }}</span>
        </div>
      </div>

      <div class="form-group">
        <label for="password">비밀번호</label>
        <div class="input-wrap">
          <input
              id="password"
              type="password"
              v-model="form.password"
              placeholder="영문, 숫자, 특수문자 중 2가지 이상 포함"
          />
          <span v-if="errors.password" class="error">{{ errors.password }}</span>
        </div>
        <div class="password-hint">
          <br />
          비밀번호는 8자 이상 ,
          영문/숫자/특수문자 중 2가지 이상 포함<br />
        </div>
      </div>

      <div class="form-group">
        <label for="confirmPassword">비밀번호 확인</label>
        <div class="input-wrap">
          <input
              id="confirmPassword"
              type="password"
              v-model="form.confirmPassword"
              placeholder="비밀번호를 다시 입력해주세요."
          />
          <span v-if="errors.confirmPassword" class="error">{{ errors.confirmPassword }}</span>
        </div>
      </div>

      <div class="form-group">
        <label for="draftLevelOptions">선출 등급</label>
        <select id="draftLevelOptions" v-model="selectedDraftLevel">
          <option
              v-for="(label, code) in draftLevelOptions"
              :key="code"
              :value="code"
          >
            {{ label }}
          </option>
        </select>
      </div>

      <button type="submit" @click="formCheck" class="submit-btn">회원가입</button>
    </form>
  </div>
</template>

<script>
import api from '@/axios.js'
import Galaxy from "../../../component/Galaxy/Galaxy.vue";

export default {
  name: 'RegisterView',
  components: {
    Galaxy
  },
  data() {
    return {
      form: {
        name: '',
        email: '',
        nickname: '',
        password: '',
        confirmPassword: '',
      },
      selectedDraftLevel: 'NONE',
      draftLevelOptions: {
        ELEMENTARY: '초등부',
        MIDDLE: '중등부',
        HIGH: '고등부',
        UNIVERSITY: '대학부',
        NONE: '해당없음',
      },
      errors: {},

      // 이메일 인증 관련
      codeSent: false,
      codeSending: false,
      verifyCode: '',
      verifying: false,
      emailVerified: false,
    }
  },
  mounted() {
    document.body.style.paddingTop = '0';
  },
  beforeUnmount() {
    document.body.style.paddingTop = '120px';
  },
  methods: {
    isValidPassword(pw) {
      const lengthOk = pw.length >= 8 && pw.length <= 32
      const hasEng = /[a-zA-Z]/.test(pw)
      const hasNum = /[0-9]/.test(pw)
      const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(pw)
      const typeCount = [hasEng, hasNum, hasSpecial].filter(Boolean).length
      const noRepeat = !/(.)\1\1/.test(pw)
      return lengthOk && typeCount >= 2 && noRepeat
    },
    isValidEmail(email) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      return emailRegex.test(email)
    },

    // 인증코드 발송
    async sendCode() {
      if (!this.form.email || !this.isValidEmail(this.form.email)) {
        this.errors = { ...this.errors, email: '유효한 이메일 주소를 입력해주세요.' }
        return
      }
      this.codeSending = true
      try {
        await api.post(`/email/send?email=${encodeURIComponent(this.form.email)}`)
        this.codeSent = true
        this.verifyCode = ''
        this.errors = { ...this.errors, email: null, verifyCode: null }
        alert('인증 코드가 발송되었습니다. 이메일을 확인해주세요.')
      } catch (err) {
        const msg = err.response?.data?.message || '인증 코드 발송에 실패했습니다.'
        alert(msg)
      } finally {
        this.codeSending = false
      }
    },

    // 인증코드 확인
    async verifyEmail() {
      if (!this.verifyCode.trim()) return
      this.verifying = true
      try {
        const res = await api.post(
            `/email/verify?email=${encodeURIComponent(this.form.email)}&code=${this.verifyCode}`
        )
        if (res.data === '인증 성공') {
          this.emailVerified = true
          this.errors = { ...this.errors, verifyCode: null, email: null }
          alert('이메일 인증이 완료되었습니다!')
        } else {
          this.errors = { ...this.errors, verifyCode: '인증 코드가 올바르지 않습니다.' }
        }
      } catch (err) {
        const msg = err.response?.data?.message || '인증 코드가 올바르지 않거나 만료되었습니다.'
        this.errors = { ...this.errors, verifyCode: msg }
      } finally {
        this.verifying = false
      }
    },

    formCheck() {
      this.errors = {}
      if (!this.form.name.trim()) {
        this.errors.name = '이름을 입력해주세요.'
      }

      if (!this.form.email.trim()) {
        this.errors.email = '이메일을 입력해주세요.'
      } else if (!this.isValidEmail(this.form.email)) {
        this.errors.email = '유효한 이메일 주소를 입력해주세요.'
      } else if (!this.emailVerified) {
        this.errors.email = '이메일 인증이 필요합니다.'
      }

      if (!this.form.nickname.trim()) {
        this.errors.nickname = '닉네임을 입력해주세요.'
      }

      if (!this.form.password.trim()) {
        this.errors.password = '비밀번호를 입력해주세요.'
      } else if (!this.isValidPassword(this.form.password)) {
        this.errors.password = '8자 이상, 영문/숫자/특수문자 중 2가지 이상, 반복 문자 금지'
      }

      if (!this.form.confirmPassword.trim()) {
        this.errors.confirmPassword = '비밀번호 확인을 입력해주세요.'
      } else if (this.form.password !== this.form.confirmPassword) {
        this.errors.confirmPassword = '비밀번호가 일치하지 않습니다.'
      }

      const isValid = Object.values(this.errors).every((e) => !e)

      if (isValid) {
        this.submit();
      }
    },
    submit() {
      api.post('/auth/register', {
        name: this.form.name,
        email: this.form.email,
        nickname: this.form.nickname,
        password: this.form.password,
        draftLevel: this.selectedDraftLevel,
      })
          .then(() => {
            alert('회원가입이 완료되었습니다!');
            this.$router.push('/login');
          })
          .catch((err) => {
            const msg = err.response?.data?.message || '오류가 발생했습니다. 관리자에게 문의하세요';
            alert(msg);
          })
    }
  },
}
</script>

<style scoped>
.email-wrap {
  display: flex;
  gap: 8px;
  align-items: center;
}

.email-wrap input {
  flex: 1;
}

.verify-btn {
  white-space: nowrap;
  padding: 8px 14px;
  font-size: 13px;
  font-weight: 600;
  border: 1px solid #3b82f6;
  border-radius: 6px;
  background: #3b82f6;
  color: #fff;
  cursor: pointer;
  transition: background 0.2s;
}

.verify-btn:hover:not(:disabled) {
  background: #2563eb;
}

.verify-btn:disabled {
  background: #9ca3af;
  border-color: #9ca3af;
  cursor: not-allowed;
}

.hint {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
  display: block;
}
</style>
