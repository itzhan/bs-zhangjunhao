<template>
  <div class="page-container">
    <div class="page-header"><h1><UserRound :size="22" style="display:inline;vertical-align:middle;margin-right:4px" /> 个人中心</h1></div>
    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px; max-width: 800px;">
      <div class="card" style="grid-column: span 2;">
        <h3 style="font-weight: 600; margin-bottom: 16px;"><FileText :size="16" style="display:inline;vertical-align:middle;margin-right:4px" /> 个人资料</h3>
        <n-form :model="profile" label-placement="left" label-width="80px">
          <n-grid :cols="2" :x-gap="16">
            <n-grid-item><n-form-item label="用户名"><n-input :value="profile.username" disabled /></n-form-item></n-grid-item>
            <n-grid-item><n-form-item label="姓名"><n-input v-model:value="profile.realName" /></n-form-item></n-grid-item>
            <n-grid-item><n-form-item label="性别"><n-select v-model:value="profile.gender" :options="genderOpts" /></n-form-item></n-grid-item>
            <n-grid-item><n-form-item label="手机号"><n-input v-model:value="profile.phone" /></n-form-item></n-grid-item>
            <n-grid-item><n-form-item label="邮箱"><n-input v-model:value="profile.email" /></n-form-item></n-grid-item>
            <n-grid-item><n-form-item label="院系"><span>{{ profile.departmentName || '-' }}</span></n-form-item></n-grid-item>
            <n-grid-item><n-form-item label="班级"><n-input v-model:value="profile.className" /></n-form-item></n-grid-item>
            <n-grid-item><n-form-item label="学号"><n-input v-model:value="profile.studentNo" /></n-form-item></n-grid-item>
          </n-grid>
          <n-button type="primary" @click="handleSaveProfile" :loading="saving">保存修改</n-button>
        </n-form>
      </div>
      <div class="card" style="grid-column: span 2;">
        <h3 style="font-weight: 600; margin-bottom: 16px;"><KeyRound :size="16" style="display:inline;vertical-align:middle;margin-right:4px" /> 修改密码</h3>
        <n-form :model="pwdForm" label-placement="left" label-width="80px" style="max-width: 400px;">
          <n-form-item label="原密码"><n-input v-model:value="pwdForm.oldPassword" type="password" /></n-form-item>
          <n-form-item label="新密码"><n-input v-model:value="pwdForm.newPassword" type="password" /></n-form-item>
          <n-button type="warning" @click="handleChangePwd">修改密码</n-button>
        </n-form>
      </div>
      <!-- 快速链接 -->
      <div class="card" style="text-align:center; cursor:pointer" @click="$router.push('/my/registrations')">
        <div><ClipboardList :size="32" color="var(--primary)" /></div><div style="font-weight:600; margin-top:8px">我的报名</div>
      </div>
      <div class="card" style="text-align:center; cursor:pointer" @click="$router.push('/my/results')">
        <div><BarChart3 :size="32" color="var(--primary)" /></div><div style="font-weight:600; margin-top:8px">我的成绩</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getProfile, updateProfile, updatePassword } from '@/api'
import { UserRound, FileText, KeyRound, ClipboardList, BarChart3 } from 'lucide-vue-next'
const profile = reactive<any>({})
const saving = ref(false)
const pwdForm = reactive({ oldPassword: '', newPassword: '' })
const genderOpts = [{ label: '未知', value: 0 }, { label: '男', value: 1 }, { label: '女', value: 2 }]

onMounted(async () => {
  try { const res:any = await getProfile(); Object.assign(profile, res.data) } catch(e){}
})
const handleSaveProfile = async () => {
  saving.value = true
  try { await updateProfile(profile); window.$message?.success('保存成功') } catch(e){} finally { saving.value = false }
}
const handleChangePwd = async () => {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) { window.$message?.warning('请填写密码'); return }
  try { await updatePassword(pwdForm); window.$message?.success('密码修改成功'); pwdForm.oldPassword=''; pwdForm.newPassword='' } catch(e){}
}
</script>
