<template>
  <div style="text-align: center; padding: 40px;">
    <h2>이메일 인증을 완료해주세요</h2>
    <p>
      입력하신 이메일 주소 <strong>{{ email }}</strong> 로 인증 메일을 보냈습니다.<br />
      메일함을 확인하시고, 링크를 클릭해 인증을 완료해주세요.
    </p>
  </div>
</template>

<script>
export default {
  name: 'VerifyEmailView',
  data() {
    return {
      email: ''
    };
  },
  mounted() {
    this.email = this.$route.query.email || '(이메일 없음)';
  }
};
</script>
