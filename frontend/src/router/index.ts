import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', component: () => import('@/views/Home.vue'), meta: { title: '首页' } },
  { path: '/login', component: () => import('@/views/Login.vue'), meta: { title: '登录', noAuth: true } },
  { path: '/register', component: () => import('@/views/Register.vue'), meta: { title: '注册', noAuth: true } },
  { path: '/events', component: () => import('@/views/Events.vue'), meta: { title: '赛事列表', noAuth: true } },
  { path: '/events/:id', component: () => import('@/views/EventDetail.vue'), meta: { title: '赛事详情', noAuth: true } },
  { path: '/sports', component: () => import('@/views/Sports.vue'), meta: { title: '运动项目', noAuth: true } },
  { path: '/announcements', component: () => import('@/views/Announcements.vue'), meta: { title: '公告通知', noAuth: true } },
  { path: '/announcements/:id', component: () => import('@/views/AnnouncementDetail.vue'), meta: { title: '公告详情', noAuth: true } },
  { path: '/ranking', component: () => import('@/views/Ranking.vue'), meta: { title: '排行榜', noAuth: true } },
  { path: '/medal-table', component: () => import('@/views/MedalTable.vue'), meta: { title: '奖牌榜', noAuth: true } },
  { path: '/my/registrations', component: () => import('@/views/MyRegistrations.vue'), meta: { title: '我的报名' } },
  { path: '/my/schedules', component: () => import('@/views/MySchedules.vue'), meta: { title: '我的赛程' } },
  { path: '/my/results', component: () => import('@/views/MyResults.vue'), meta: { title: '我的成绩' } },
  { path: '/my/awards', component: () => import('@/views/MyAwards.vue'), meta: { title: '我的奖项' } },
  { path: '/profile', component: () => import('@/views/Profile.vue'), meta: { title: '个人中心' } },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = (to.meta.title as string || '高校运动会管理系统') + ' - 高校运动会管理系统'
  const token = localStorage.getItem('token')
  if (!to.meta.noAuth && !token && to.path !== '/login') {
    next('/login')
  } else {
    next()
  }
})

export default router
