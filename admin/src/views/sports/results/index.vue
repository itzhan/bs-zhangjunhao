<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NPopconfirm, NInput, NSelect, NDataTable, NModal, NForm, NFormItem, NGrid, NGridItem, NInputNumber, useMessage } from 'naive-ui';
import { fetchResultList, createResult, updateResult, deleteResult, fetchEventList, fetchSportList, fetchUserList } from '@/service/api/management';
const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const eventOptions = ref<any[]>([]);
const sportOptions = ref<any[]>([]);
const userOptions = ref<any[]>([]);
const userLoading = ref(false);
const query = reactive({ page: 1, size: 10, eventId: null as any, sportId: null as any });
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive<any>({ eventId: null, sportId: null, userId: null, score: '', ranking: null, isRecord: 0, remark: '' });
const rankingTagTypes = ['warning', 'default', 'info'] as const;
const columns: any[] = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '姓名', key: 'userName' },
  { title: '赛事', key: 'eventName' },
  { title: '项目', key: 'sportName' },
  { title: '成绩', key: 'score', render: (r: any) => h('span', { style: 'font-weight:700;color:#F97316' }, r.score) },
  { title: '名次', key: 'ranking', render: (r: any) => r.ranking <= 3 ? h(NTag, { type: rankingTagTypes[r.ranking - 1], size: 'small', round: true }, () => ['第1名', '第2名', '第3名'][r.ranking - 1]) : r.ranking },
  { title: '破纪录', key: 'isRecord', render: (r: any) => r.isRecord ? h(NTag, { type: 'warning', size: 'small' }, () => '破纪录') : '' },
  { title: '操作', key: 'actions', width: 150, render: (r: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', onClick: () => handleEdit(r) }, () => '编辑'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(r.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确定删除？' })
  ]) }
];
async function loadData() {
  loading.value = true;
  try {
    const res = await fetchResultList(query);
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
async function loadEvents() {
  try {
    const res = await fetchEventList({ page: 1, size: 100 });
    const page = res.data as any;
    if (!res.error && page) {
      eventOptions.value = (page.records || []).map((e: any) => ({ label: e.name, value: e.id }));
    }
  } catch (e) {
  }
}
async function loadSports(eid: number) {
  try {
    const res = await fetchSportList({ page: 1, size: 200, eventId: eid });
    const page = res.data as any;
    if (!res.error && page) {
      sportOptions.value = (page.records || []).map((s: any) => ({ label: s.name, value: s.id }));
    }
  } catch (e) {
  }
}

function buildUserOption(user: any) {
  const name = user.realName || user.userName || user.username || `用户${user.id}`;
  const extra = [user.studentNo, user.username].filter(Boolean).join(' / ');
  return {
    label: extra ? `${name}（${extra}）` : name,
    value: user.id
  };
}

async function searchUsers(keyword = '') {
  userLoading.value = true;
  try {
    const res = await fetchUserList({ page: 1, size: 20, role: 'USER', keyword: keyword || undefined });
    const page = res.data as any;
    if (!res.error && page) {
      userOptions.value = (page.records || []).map((user: any) => buildUserOption(user));
    }
  } catch (e) {
  } finally {
    userLoading.value = false;
  }
}

async function handleAdd() {
  isEdit.value = false;
  Object.assign(form, { id: null, eventId: null, sportId: null, userId: null, score: '', ranking: null, isRecord: 0, remark: '' });
  sportOptions.value = [];
  await searchUsers();
  showModal.value = true;
}

async function handleEdit(row: any) {
  isEdit.value = true;
  Object.assign(form, { ...row });
  if (row.eventId) loadSports(row.eventId);
  userOptions.value = row.userId ? [{ label: row.userName || `用户${row.userId}`, value: row.userId }] : [];
  showModal.value = true;
}

async function handleSave() {
  if (!form.userId) {
    message.warning('请选择录入成绩的用户');
    return;
  }
  try {
    if (isEdit.value) {
      await updateResult(form.id, form);
    } else {
      await createResult(form);
    }
    message.success('保存成功');
    showModal.value = false;
    loadData();
  } catch (e) {}
}
async function handleDelete(id: number) { try { await deleteResult(id); message.success('删除成功'); loadData(); } catch(e) {} }
function handlePageChange(p: number) { query.page = p; loadData(); }
onMounted(() => { loadEvents(); loadData(); });
</script>
<template>
  <div class="p-16px">
    <NSpace class="mb-16px" justify="space-between">
      <NSpace><NSelect v-model:value="query.eventId" :options="eventOptions" placeholder="筛选赛事" clearable style="width:200px" @update:value="(v:any) => { loadData(); if(v) loadSports(v); }" />
        <NSelect v-model:value="query.sportId" :options="sportOptions" placeholder="筛选项目" clearable style="width:200px" @update:value="loadData" />
        <NButton type="primary" @click="loadData">搜索</NButton></NSpace>
      <NButton type="primary" @click="handleAdd">录入成绩</NButton>
    </NSpace>
    <NDataTable :columns="columns" :data="data" :loading="loading" :bordered="false" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" remote />
    <NModal v-model:show="showModal" preset="card" :title="isEdit ? '编辑成绩' : '录入成绩'" style="width:650px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NGrid :cols="2" :x-gap="16">
          <NGridItem><NFormItem label="赛事"><NSelect v-model:value="form.eventId" :options="eventOptions" @update:value="loadSports" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="项目"><NSelect v-model:value="form.sportId" :options="sportOptions" /></NFormItem></NGridItem>
          <NGridItem>
            <NFormItem label="参赛用户">
              <NSelect
                v-model:value="form.userId"
                filterable
                remote
                clearable
                placeholder="输入姓名搜索用户"
                :options="userOptions"
                :loading="userLoading"
                @search="searchUsers"
              />
            </NFormItem>
          </NGridItem>
          <NGridItem><NFormItem label="成绩"><NInput v-model:value="form.score" placeholder="如 10.5" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="名次"><NInputNumber v-model:value="form.ranking" :min="1" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="破纪录"><NSelect v-model:value="form.isRecord" :options="[{label:'否',value:0},{label:'是',value:1}]" /></NFormItem></NGridItem>
          <NGridItem :span="2"><NFormItem label="备注"><NInput v-model:value="form.remark" /></NFormItem></NGridItem>
        </NGrid>
      </NForm>
      <template #action><NButton type="primary" @click="handleSave">保存</NButton></template>
    </NModal>
  </div>
</template>
