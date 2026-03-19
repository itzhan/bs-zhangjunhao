<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NPopconfirm, NInput, NSelect, NDataTable, NModal, NForm, NFormItem, NGrid, NGridItem, useMessage } from 'naive-ui';
import { fetchAwardList, createAward, updateAward, deleteAward, fetchEventList, fetchSportList, fetchUserList } from '@/service/api/management';
const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const eventOptions = ref<any[]>([]);
const sportOptions = ref<any[]>([]);
const userOptions = ref<any[]>([]);
const userLoading = ref(false);
const query = reactive({ page: 1, size: 10, eventId: null as any });
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive<any>({ eventId: null, sportId: null, userId: null, awardType: 'GOLD', awardName: '', certificateNo: '' });
const awardTypeOpts = [{ label: '金牌', value: 'GOLD' }, { label: '银牌', value: 'SILVER' }, { label: '铜牌', value: 'BRONZE' }, { label: '其他', value: 'OTHER' }];
const awardTagType: any = { GOLD: 'warning', SILVER: 'default', BRONZE: 'info', OTHER: 'success' };
const columns: any[] = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '姓名', key: 'userName' },
  { title: '赛事', key: 'eventName' },
  { title: '项目', key: 'sportName' },
  { title: '奖项', key: 'awardType', render: (r: any) => h(NTag, { type: awardTagType[r.awardType], size: 'small' }, () => r.awardType === 'GOLD' ? '金牌' : r.awardType === 'SILVER' ? '银牌' : r.awardType === 'BRONZE' ? '铜牌' : r.awardName) },
  { title: '奖项名称', key: 'awardName' },
  { title: '证书编号', key: 'certificateNo' },
  { title: '操作', key: 'actions', width: 150, render: (r: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', onClick: () => handleEdit(r) }, () => '编辑'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(r.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确定删除？' })
  ]) }
];
async function loadData() {
  loading.value = true;
  try {
    const res = await fetchAwardList(query);
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

function syncAwardNameByType(type: string) {
  if (type === 'GOLD') form.awardName = '金牌';
  else if (type === 'SILVER') form.awardName = '银牌';
  else if (type === 'BRONZE') form.awardName = '铜牌';
  else if (!form.awardName || ['金牌', '银牌', '铜牌'].includes(form.awardName)) form.awardName = '';
}

async function handleAdd() {
  isEdit.value = false;
  Object.assign(form, { id: null, eventId: null, sportId: null, userId: null, awardType: 'GOLD', awardName: '金牌', certificateNo: '' });
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
    message.warning('请选择获奖用户');
    return;
  }
  try {
    if (isEdit.value) {
      await updateAward(form.id, form);
    } else {
      await createAward(form);
    }
    message.success('保存成功');
    showModal.value = false;
    loadData();
  } catch (e) {}
}
async function handleDelete(id: number) { try { await deleteAward(id); message.success('删除成功'); loadData(); } catch(e) {} }
function handlePageChange(p: number) { query.page = p; loadData(); }
onMounted(() => { loadEvents(); loadData(); });
</script>
<template>
  <div class="p-16px">
    <NSpace class="mb-16px" justify="space-between">
      <NSpace><NSelect v-model:value="query.eventId" :options="eventOptions" placeholder="筛选赛事" clearable style="width:200px" @update:value="loadData" /><NButton type="primary" @click="loadData">搜索</NButton></NSpace>
      <NButton type="primary" @click="handleAdd">颁发奖项</NButton>
    </NSpace>
    <NDataTable :columns="columns" :data="data" :loading="loading" :bordered="false" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" remote />
    <NModal v-model:show="showModal" preset="card" :title="isEdit ? '编辑奖项' : '颁发奖项'" style="width:650px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NGrid :cols="2" :x-gap="16">
          <NGridItem><NFormItem label="赛事"><NSelect v-model:value="form.eventId" :options="eventOptions" @update:value="loadSports" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="项目"><NSelect v-model:value="form.sportId" :options="sportOptions" /></NFormItem></NGridItem>
          <NGridItem>
            <NFormItem label="获奖用户">
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
          <NGridItem><NFormItem label="奖项类型"><NSelect v-model:value="form.awardType" :options="awardTypeOpts" @update:value="syncAwardNameByType" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="奖项名称"><NInput v-model:value="form.awardName" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="证书编号"><NInput v-model:value="form.certificateNo" /></NFormItem></NGridItem>
        </NGrid>
      </NForm>
      <template #action><NButton type="primary" @click="handleSave">保存</NButton></template>
    </NModal>
  </div>
</template>
