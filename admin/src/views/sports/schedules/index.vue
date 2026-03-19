<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NPopconfirm, NInput, NSelect, NDataTable, NModal, NForm, NFormItem, NGrid, NGridItem, NInputNumber, useMessage } from 'naive-ui';
import { fetchScheduleList, createSchedule, updateSchedule, deleteSchedule, fetchEventList, fetchSportList } from '@/service/api/management';
const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const eventOptions = ref<any[]>([]);
const sportOptions = ref<any[]>([]);
const query = reactive({ page: 1, size: 10, eventId: null as any, sportId: null as any, status: null as any });
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive<any>({ sportId: null, eventId: null, roundName: '', matchDate: '', startTime: '', endTime: '', venue: '', groupNo: 1, description: '', status: 0 });
const columns: any[] = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '赛事', key: 'eventName' },
  { title: '项目', key: 'sportName' },
  { title: '轮次', key: 'roundName' },
  { title: '日期', key: 'matchDate' },
  { title: '时间', key: 'startTime', render: (r: any) => `${r.startTime || ''} - ${r.endTime || ''}` },
  { title: '场地', key: 'venue' },
  { title: '组号', key: 'groupNo' },
  { title: '状态', key: 'status', render: (r: any) => h(NTag, { type: r.status === 0 ? 'info' : r.status === 1 ? 'warning' : 'success', size: 'small' }, () => r.status === 0 ? '未开始' : r.status === 1 ? '进行中' : '已结束') },
  { title: '操作', key: 'actions', width: 150, render: (r: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', onClick: () => handleEdit(r) }, () => '编辑'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(r.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确定删除？' })
  ]) }
];
async function loadData() {
  loading.value = true;
  try {
    const res = await fetchScheduleList(query);
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
function handleAdd() { isEdit.value = false; Object.assign(form, { id: null, sportId: null, eventId: null, roundName: '预赛', matchDate: '', startTime: '', endTime: '', venue: '', groupNo: 1, description: '', status: 0 }); showModal.value = true; }
function handleEdit(row: any) { isEdit.value = true; Object.assign(form, { ...row }); if (row.eventId) loadSports(row.eventId); showModal.value = true; }
async function handleSave() { try { if (isEdit.value) { await updateSchedule(form.id, form); } else { await createSchedule(form); } message.success('保存成功'); showModal.value = false; loadData(); } catch(e) {} }
async function handleDelete(id: number) { try { await deleteSchedule(id); message.success('删除成功'); loadData(); } catch(e) {} }
function handlePageChange(p: number) { query.page = p; loadData(); }
onMounted(() => { loadEvents(); loadData(); });
</script>
<template>
  <div class="p-16px">
    <NSpace class="mb-16px" justify="space-between">
      <NSpace><NSelect v-model:value="query.eventId" :options="eventOptions" placeholder="筛选赛事" clearable style="width:200px" @update:value="loadData" /><NButton type="primary" @click="loadData">搜索</NButton></NSpace>
      <NButton type="primary" @click="handleAdd">新增赛程</NButton>
    </NSpace>
    <NDataTable :columns="columns" :data="data" :loading="loading" :bordered="false" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" remote />
    <NModal v-model:show="showModal" preset="card" :title="isEdit ? '编辑赛程' : '新增赛程'" style="width:700px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NGrid :cols="2" :x-gap="16">
          <NGridItem><NFormItem label="赛事"><NSelect v-model:value="form.eventId" :options="eventOptions" @update:value="loadSports" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="项目"><NSelect v-model:value="form.sportId" :options="sportOptions" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="轮次"><NInput v-model:value="form.roundName" placeholder="预赛/决赛" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="日期"><NInput v-model:value="form.matchDate" placeholder="2025-05-01" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="开始时间"><NInput v-model:value="form.startTime" placeholder="09:00:00" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="结束时间"><NInput v-model:value="form.endTime" placeholder="10:30:00" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="场地"><NInput v-model:value="form.venue" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="组号"><NInputNumber v-model:value="form.groupNo" :min="1" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="状态"><NSelect v-model:value="form.status" :options="[{label:'未开始',value:0},{label:'进行中',value:1},{label:'已结束',value:2}]" /></NFormItem></NGridItem>
        </NGrid>
      </NForm>
      <template #action><NButton type="primary" @click="handleSave">保存</NButton></template>
    </NModal>
  </div>
</template>
