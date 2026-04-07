<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NPopconfirm, NInput, NSelect, NDataTable, NModal, NForm, NFormItem, NGrid, NGridItem, NInputNumber, useMessage } from 'naive-ui';
import { fetchUserList, updateUser, deleteUser } from '@/service/api/management';
const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const query = reactive({ page: 1, size: 10, keyword: '', role: '' });
const showModal = ref(false);
const form = reactive<any>({});
const roleOptions = [{ label: '全部', value: '' }, { label: '管理员', value: 'ADMIN' }, { label: '裁判员', value: 'REFEREE' }, { label: '普通用户', value: 'USER' }];
const genderOptions = [{ label: '未知', value: 0 }, { label: '男', value: 1 }, { label: '女', value: 2 }];
const statusOptions = [{ label: '正常', value: 1 }, { label: '禁用', value: 0 }];

const columns: any[] = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '用户名', key: 'username' },
  { title: '姓名', key: 'realName' },
  { title: '学号', key: 'studentNo' },
  { title: '院系', key: 'departmentName' },
  { title: '角色', key: 'role', render: (r: any) => h(NTag, { type: r.role === 'ADMIN' ? 'error' : r.role === 'REFEREE' ? 'warning' : 'info', size: 'small' }, () => r.role === 'ADMIN' ? '管理员' : r.role === 'REFEREE' ? '裁判员' : '用户') },
  { title: '状态', key: 'status', render: (r: any) => h(NTag, { type: r.status === 1 ? 'success' : 'error', size: 'small' }, () => r.status === 1 ? '正常' : '禁用') },
  { title: '操作', key: 'actions', width: 150, render: (r: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', onClick: () => handleEdit(r) }, () => '编辑'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(r.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确定删除？' })
  ]) }
];

async function loadData() {
  loading.value = true;
  try {
    const res = await fetchUserList(query);
    const page = res.data as any;
    if (!res.error && page) {
      data.value = page.records || [];
      total.value = page.total || 0;
    }
  } catch (e) {
  } finally {
    loading.value = false;
  }
}
function handleEdit(row: any) { Object.assign(form, { ...row }); showModal.value = true; }
async function handleSave() {
  try { await updateUser(form.id, form); message.success('保存成功'); showModal.value = false; loadData(); } catch(e) {}
}
async function handleDelete(id: number) {
  try { await deleteUser(id); message.success('删除成功'); loadData(); } catch(e) {}
}
function handlePageChange(p: number) { query.page = p; loadData(); }
onMounted(loadData);
</script>

<template>
  <div class="p-16px">
    <NSpace class="mb-16px" justify="space-between">
      <NSpace><NInput v-model:value="query.keyword" placeholder="搜索用户名/姓名/学号" clearable @keyup.enter="loadData" style="width:250px" />
        <NSelect v-model:value="query.role" :options="roleOptions" style="width:120px" @update:value="loadData" />
        <NButton type="primary" @click="loadData">搜索</NButton></NSpace>
    </NSpace>
    <NDataTable :columns="columns" :data="data" :loading="loading" :bordered="false" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" remote />
    <NModal v-model:show="showModal" preset="card" title="编辑用户" style="width:600px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NGrid :cols="2" :x-gap="16">
          <NGridItem><NFormItem label="姓名"><NInput v-model:value="form.realName" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="性别"><NSelect v-model:value="form.gender" :options="genderOptions" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="手机号"><NInput v-model:value="form.phone" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="邮箱"><NInput v-model:value="form.email" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="角色"><NSelect v-model:value="form.role" :options="[{label:'管理员',value:'ADMIN'},{label:'裁判员',value:'REFEREE'},{label:'用户',value:'USER'}]" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="状态"><NSelect v-model:value="form.status" :options="statusOptions" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="新密码"><NInput v-model:value="form.password" placeholder="留空不修改" /></NFormItem></NGridItem>
        </NGrid>
      </NForm>
      <template #action><NButton type="primary" @click="handleSave">保存</NButton></template>
    </NModal>
  </div>
</template>
