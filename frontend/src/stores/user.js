import { defineStore } from 'pinia'
import api from '@/axios'

export const useUserStore = defineStore('user', {
    state: () => ({
        currentUser: null,
    }),
    actions: {
        setUser(user) {
            this.currentUser = user
        },
        logout() {
            this.$reset();
            localStorage.removeItem("refreshToken");
        },
        async fetchCurrentUser(email, password) {
            const token = localStorage.getItem('accessToken')
            if (!token) {
                return ;
            }

            try {
                const res = await api.get('/users/me')
                this.setUser(res.data)
            } catch (err) {
                if (err.response?.status === 401 || err.response?.status === 403) {
                    this.logout()
                } else {
                    console.error('유저 정보 가져오기 실패:', err)
                }
            }
        },
    },
    persist: {
        storage : localStorage,
    },
})
