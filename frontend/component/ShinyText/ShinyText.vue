<template>
  <div
      class="shiny-text"
      :class="[disabled ? 'disabled' : '', className]"
      :style="{ animationDuration: speed + 's' }"
  >
    {{ text }}
  </div>
</template>

<script setup>
defineProps({
  text: { type: String, required: true },
  disabled: { type: Boolean, default: false },
  speed: { type: Number, default: 5 },
  className: { type: String, default: '' }
})
</script>

<style scoped>
.shiny-text {
  font-size: 3rem;
  color: #fff;
  background: linear-gradient(
      90deg,
      red 0%,
      yellow 50%,
      blue 100%
  );
  background-size: 200% 100%;
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  animation: shine 2s linear infinite;
}

@keyframes shine {
  0% {
    background-position: 100%;
  }
  100% {
    background-position: -100%;
  }
}

.shiny-text.disabled {
  animation: none;
}
</style>
