import { createRouter, createWebHistory } from 'vue-router'

import Login from '@/views/auth/Login.vue'
import TeamList from '@/views/teams/TeamList.vue'
import Community from '@/views/community/Community.vue'
import TeamCreateModal from '@/views/teams/TeamCreateModal.vue'
import Register from '@/views/auth/Register.vue'
import Home from '@/views/Home.vue'
import MyTeam from "@/views/myTeam/MyTeam.vue";
import PostDetail from "@/views/community/PostDetail.vue";
import WritePost from "@/views/community/WritePost.vue";
import {useUserStore} from "@stores/user.js";


const routes = [
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: { hideHeader: true },

    },
    {
        path: '/register',
        name: 'Register',
        component: Register,
        meta: { hideHeader: true },
    },
    {
        path: '/teams',
        name: 'TeamList',
        component: TeamList,
        meta: { requiresAuth: true }
    },
    {
        path: '/teams/:id',
        name: 'MyTeam',
        component: MyTeam,
        meta: { requiresAuth: true }
    },
    {
        path: '/community',
        name: 'Community',
        component: Community,
        meta: { requiresAuth: true }
    },
    {
        path: '/community/write',
        name: 'WritePost',
        component: WritePost
    },
    {
        path: '/teams/create',
        name: 'TeamCreateModal',
        component: TeamCreateModal,
        meta: { requiresAuth: true }
    },
    {
        path: '/',
        name: 'Home',
        component: Home
    },
    {
        path: '/post/:id',
        name: 'PostDetail',
        component: PostDetail,


    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});
router.beforeEach((to, from, next) => {

    const isLoggedIn = !!localStorage.getItem('accessToken');

    if (!isLoggedIn) {
        if (to.path === '/login' || to.path === '/register') {
            next();
        } else {
            next('/login');
        }
    } else {
        if (to.path === '/login' || to.path === '/register') {
            next('/');
        } else {
            next();
        }
    }
});

export default router;
