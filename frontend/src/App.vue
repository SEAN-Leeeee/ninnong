<template>
  <div id="app">
    <Header v-if="!$route.meta.hideHeader" />
    <router-view />
  </div>
</template>

<script setup>
import Header from "@/views/layout/Header.vue";
import { onMounted } from 'vue'
import { useUserStore } from '@stores/user'

const userStore = useUserStore()

onMounted(async () => {
  await userStore.fetchCurrentUser()
})
</script>
