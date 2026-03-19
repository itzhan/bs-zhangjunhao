<template>
  <div style="min-height: 100vh; display: flex; align-items: center; justify-content: center;
    background: linear-gradient(135deg, #EEF2FF 0%, #F0F9FF 50%, #FFF7ED 100%);">
    <div class="card fade-in" style="width: 480px; padding: 40px;">
      <div style="text-align: center; margin-bottom: 32px;">
        <div style="margin-bottom: 8px;"><PersonStanding :size="48" color="var(--primary)" /></div>
        <h1 style="font-size: 24px; font-weight: 700; color: var(--primary);">注册参赛账号</h1>
      </div>
      <n-form :model="form" :rules="rules">
        <n-grid :cols="2" :x-gap="16">
          <n-grid-item>
            <n-form-item label="用户名" path="username">
              <n-input v-model:value="form.username" placeholder="3-50位" />
            </n-form-item>
          </n-grid-item>
          <n-grid-item>
            <n-form-item label="密码" path="password">
              <n-input v-model:value="form.password" type="password" placeholder="6位以上" />
            </n-form-item>
          </n-grid-item>
          <n-grid-item>
            <n-form-item label="姓名" path="realName">
              <n-input v-model:value="form.realName" placeholder="真实姓名" />
            </n-form-item>
          </n-grid-item>
          <n-grid-item>
            <n-form-item label="性别">
              <n-select v-model:value="form.gender" :options="genderOptions" />
            </n-form-item>
          </n-grid-item>
          <n-grid-item>
            <n-form-item label="院系">
              <n-select v-model:value="form.departmentId" :options="deptOptions" placeholder="选择院系" />
            </n-form-item>
          </n-grid-item>
          <n-grid-item>
            <n-form-item label="班级">
              <n-input v-model:value="form.className" placeholder="如：计科2201" />
            </n-form-item>
          </n-grid-item>
          <n-grid-item>
            <n-form-item label="学号">
              <n-input v-model:value="form.studentNo" />
            </n-form-item>
          </n-grid-item>
          <n-grid-item>
            <n-form-item label="手机号">
              <n-input v-model:value="form.phone" />
            </n-form-item>
          </n-grid-item>
        </n-grid>
        <n-button type="primary" block size="large" :loading="loading" @click="handleRegister" style="margin-top: 8px;">
          注 册
        </n-button>
      </n-form>
      <div style="text-align: center; margin-top: 20px; font-size: 14px; color: var(--text-muted);">
        已有账号？<router-link to="/login" style="color: var(--primary-light);">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { register, getDepartments } from '@/api'
import { PersonStanding } from 'lucide-vue-next'

const router = useRouter()
const loading = ref(false)
const deptOptions = ref<any[]>([])
const genderOptions = [
  { label: '未知', value: 0 },
  { label: '男', value: 1 },
  { label: '女', value: 2 }
]
const form = reactive({
  username: '', password: '', realName: '', gender: 1,
  phone: '', departmentId: null as any, className: '', studentNo: ''
})
const rules = {
  username: { required: true, message: '请输入用户名' },
  password: { required: true, message: '请输入密码' },
  realName: { required: true, message: '请输入姓名' }
}

onMounted(async () => {
  try {
    const res: any = await getDepartments()
    deptOptions.value = (res.data || []).map((d: any) => ({ label: d.name, value: d.id }))
  } catch (e) {}
})

const handleRegister = async () => {
  if (!form.username || !form.password || !form.realName) return
  loading.value = true
  try {
    await register(form)
    window.$message?.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
  } finally {
    loading.value = false
  }
}
</script>
