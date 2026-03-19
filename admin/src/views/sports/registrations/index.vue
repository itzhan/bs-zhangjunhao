<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NInput, NSelect, NDataTable, NModal, useMessage } from 'naive-ui';
import { fetchRegistrationList, approveRegistration, rejectRegistration, batchApproveRegistrations, fetchEventList } from '@/service/api/management';
const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const eventOptions = ref<any[]>([]);
const query = reactive({ page: 1, size: 10, eventId: null as any, sportId: null as any, status: null as any, keyword: '' });
const checkedKeys = ref<number[]>([]);
const showRejectModal = ref(false);
const rejectId = ref(0);
const rejectReason = ref('');
const statusOptions: any[] = [{ label: '全部', value: null }, { label: '待审核', value: 0 }, { label: '已通过', value: 1 }, { label: '已拒绝', value: 2 }];

const statusMap: any = { 0: { text: '待审核', type: 'warning' }, 1: { text: '已通过', type: 'success' }, 2: { text: '已拒绝', type: 'error' }, 3: { text: '已取消', type: 'default' } };
const columns: any[] = [
  { type: 'selection' as const },
  { title: 'ID', key: 'id', width: 60 },
  { title: '姓名', key: 'userName' },
  { title: '学号', key: 'studentNo' },
  { title: '院系', key: 'departmentName' },
  { title: '赛事', key: 'eventName' },
  { title: '项目', key: 'sportName' },
  { title: '状态', key: 'status', render: (r: any) => h(NTag, { type: statusMap[r.status]?.type, size: 'small' }, () => statusMap[r.status]?.text) },
  { title: '操作', key: 'actions', width: 200, render: (r: any) => r.status === 0 ? h(NSpace, null, () => [
    h(NButton, { size: 'small', type: 'success', onClick: () => handleApprove(r.id) }, () => '通过'),
    h(NButton, { size: 'small', type: 'error', onClick: () => { rejectId.value = r.id; rejectReason.value = ''; showRejectModal.value = true; } }, () => '拒绝')
  ]) : null }
];

async function loadData() {
  loading.value = true;
  try {
    const res = await fetchRegistrationList(query);
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
async function handleApprove(id: number) { try { await approveRegistration(id); message.success('已通过'); loadData(); } catch(e) {} }
async function handleReject() { try { await rejectRegistration(rejectId.value, rejectReason.value); message.success('已拒绝'); showRejectModal.value = false; loadData(); } catch(e) {} }
async function handleBatchApprove() { if (checkedKeys.value.length === 0) { message.warning('请选择报名记录'); return; }
  try { await batchApproveRegistrations(checkedKeys.value); message.success('批量通过成功'); checkedKeys.value = []; loadData(); } catch(e) {} }
function handlePageChange(p: number) { query.page = p; loadData(); }
onMounted(() => { loadEvents(); loadData(); });
</script>
<template>
  <div class="p-16px">
    <NSpace class="mb-16px" justify="space-between">
      <NSpace><NSelect v-model:value="query.eventId" :options="eventOptions" placeholder="筛选赛事" clearable style="width:200px" @update:value="loadData" />
        <NSelect v-model:value="query.status" :options="statusOptions" style="width:120px" @update:value="loadData" />
        <NButton type="primary" @click="loadData">搜索</NButton></NSpace>
      <NButton type="warning" @click="handleBatchApprove">批量通过</NButton>
    </NSpace>
    <NDataTable :columns="columns" :data="data" :loading="loading" :bordered="false" :row-key="(r:any)=>r.id"
      v-model:checked-row-keys="checkedKeys"
      :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" remote />
    <NModal v-model:show="showRejectModal" preset="card" title="拒绝原因" style="width:400px">
      <NInput type="textarea" v-model:value="rejectReason" placeholder="请输入拒绝原因" :rows="3" />
      <template #action><NButton type="error" @click="handleReject">确认拒绝</NButton></template>
    </NModal>
  </div>
</template>
