<template>
  <div style="min-height: 100vh; display: flex; align-items: center; justify-content: center;
    background: linear-gradient(135deg, #EEF2FF 0%, #F0F9FF 50%, #FFF7ED 100%);">
    <div class="card fade-in" style="width: 400px; padding: 40px;">
      <div style="text-align: center; margin-bottom: 32px;">
        <div style="margin-bottom: 8px;"><Trophy :size="48" color="var(--primary)" /></div>
        <h1 style="font-size: 24px; font-weight: 700; color: var(--primary);">高校运动会管理系统</h1>
        <p style="color: var(--text-muted); font-size: 14px; margin-top: 8px;">登录您的账号</p>
      </div>
      <n-form ref="formRef" :model="form" :rules="rules">
        <n-form-item path="username" label="用户名">
          <n-input v-model:value="form.username" placeholder="请输入用户名" @keyup.enter="handleLogin" />
        </n-form-item>
        <n-form-item path="password" label="密码">
          <n-input v-model:value="form.password" type="password" show-password-on="click"
                   placeholder="请输入密码" @keyup.enter="handleLogin" />
        </n-form-item>
        <n-button type="primary" block size="large" :loading="loading" @click="handleLogin"
                  style="margin-top: 8px;">
          登 录
        </n-button>
      </n-form>
      <div style="text-align: center; margin-top: 20px; font-size: 14px; color: var(--text-muted);">
        还没有账号？<router-link to="/register" style="color: var(--primary-light);">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api'
import { Trophy } from 'lucide-vue-next'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: { required: true, message: '请输入用户名' },
  password: { required: true, message: '请输入密码' }
}

const handleLogin = async () => {
  if (!form.username || !form.password) return
  loading.value = true
  try {
    const res: any = await login(form)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data))
    window.$message?.success('登录成功')
    router.push('/')
  } catch (e) {
  } finally {
    loading.value = false
  }
}
</script>
