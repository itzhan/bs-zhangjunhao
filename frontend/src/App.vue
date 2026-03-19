<template>
  <n-message-provider>
    <n-dialog-provider>
      <n-notification-provider>
        <app-content />
      </n-notification-provider>
    </n-dialog-provider>
  </n-message-provider>
</template>

<script setup lang="ts">
import { defineComponent, h, ref, watch } from 'vue'
import { NMessageProvider, NDialogProvider, NNotificationProvider, NButton, useMessage } from 'naive-ui'
import { RouterView, RouterLink, useRouter, useRoute } from 'vue-router'
import { Trophy } from 'lucide-vue-next'

// 内部组件，用于获取 message 实例
const AppContent = defineComponent({
  setup() {
    const message = useMessage()
    window.$message = message
    const router = useRouter()
    const route = useRoute()
    const token = ref<string | null>(null)
    const user = ref<any>(null)

    const syncAuthState = () => {
      token.value = localStorage.getItem('token')
      user.value = JSON.parse(localStorage.getItem('user') || 'null')
    }

    watch(() => route.fullPath, syncAuthState, { immediate: true })

    const handleLogout = () => {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      syncAuthState()
      router.push('/login')
    }

    return () => h('div', { class: 'app' }, [
      // 导航栏
      h('nav', { class: 'navbar' }, [
        h('div', { class: 'navbar-inner' }, [
          h(RouterLink, { to: '/', class: 'navbar-logo' }, () => [
            h(Trophy, { size: 20, class: 'logo-icon' }),
            h('span', null, '运动会管理系统')
          ]),
          h('div', { class: 'navbar-nav' }, [
            h(RouterLink, { to: '/', class: route.path === '/' ? 'active' : '' }, () => '首页'),
            h(RouterLink, { to: '/events', class: route.path.startsWith('/events') ? 'active' : '' }, () => '赛事'),
            h(RouterLink, { to: '/announcements', class: route.path.startsWith('/announcements') ? 'active' : '' }, () => '公告'),
            h(RouterLink, { to: '/ranking', class: route.path === '/ranking' ? 'active' : '' }, () => '排行榜'),
            h(RouterLink, { to: '/medal-table', class: route.path === '/medal-table' ? 'active' : '' }, () => '奖牌榜'),
          ]),
          h('div', { class: 'navbar-auth' },
            token.value
              ? [
                  h(RouterLink, { to: '/profile' }, () =>
                    h(NButton, { quaternary: true, size: 'small' }, () => user.value?.realName || '个人中心')
                  ),
                  h(NButton, { size: 'small', onClick: handleLogout }, () => '退出登录')
                ]
              : [
                  h(RouterLink, { to: '/login' }, () => h(NButton, { size: 'small' }, () => '登录')),
                  h(RouterLink, { to: '/register' }, () =>
                    h(NButton, { type: 'primary', size: 'small' }, () => '注册')
                  )
                ]
          )
        ])
      ]),
      // 页面内容
      h(RouterView),
      // 底部
      h('footer', { class: 'footer' }, [
        h('div', { class: 'footer-inner' }, [
          h('p', null, '高校运动会管理系统 — 让赛事管理更智能'),
          h('div', { class: 'copyright' }, '© 2025 张君豪 毕业设计作品')
        ])
      ])
    ])
  }
})
</script>

<style>
/* 全局 $message 类型声明由 env.d.ts 处理 */
</style>
