<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NPopconfirm, NInput, NSelect, NDataTable, NModal, NForm, NFormItem, NGrid, NGridItem, NDatePicker, useMessage } from 'naive-ui';
import { fetchEventList, createEvent, updateEvent, deleteEvent } from '@/service/api/management';
const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const query = reactive({ page: 1, size: 10, keyword: '', status: null as any });
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive<any>({ name: '', description: '', location: '', status: 0 });
const statusMap: any = { 0: { text: '筹备中', type: 'default' }, 1: { text: '报名中', type: 'success' }, 2: { text: '进行中', type: 'warning' }, 3: { text: '已结束', type: 'info' } };
const columns: any[] = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '赛事名称', key: 'name' },
  { title: '开始日期', key: 'startDate' },
  { title: '结束日期', key: 'endDate' },
  { title: '地点', key: 'location' },
  { title: '状态', key: 'status', render: (r: any) => h(NTag, { type: statusMap[r.status]?.type, size: 'small' }, () => statusMap[r.status]?.text) },
  { title: '操作', key: 'actions', width: 150, render: (r: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', onClick: () => handleEdit(r) }, () => '编辑'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(r.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确定删除？' })
  ]) }
];
async function loadData() {
  loading.value = true;
  try {
    const res = await fetchEventList(query);
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
function handleAdd() { isEdit.value = false; Object.assign(form, { id: null, name: '', description: '', location: '', startDate: '', endDate: '', status: 0, maxParticipants: null }); showModal.value = true; }
function handleEdit(row: any) { isEdit.value = true; Object.assign(form, { ...row }); showModal.value = true; }
async function handleSave() { try { if (isEdit.value) { await updateEvent(form.id, form); } else { await createEvent(form); } message.success('保存成功'); showModal.value = false; loadData(); } catch(e) {} }
async function handleDelete(id: number) { try { await deleteEvent(id); message.success('删除成功'); loadData(); } catch(e) {} }
function handlePageChange(p: number) { query.page = p; loadData(); }
onMounted(loadData);
</script>
<template>
  <div class="p-16px">
    <NSpace class="mb-16px" justify="space-between">
      <NSpace><NInput v-model:value="query.keyword" placeholder="搜索赛事名称" clearable @keyup.enter="loadData" style="width:200px" /><NButton type="primary" @click="loadData">搜索</NButton></NSpace>
      <NButton type="primary" @click="handleAdd">新增赛事</NButton>
    </NSpace>
    <NDataTable :columns="columns" :data="data" :loading="loading" :bordered="false" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" remote />
    <NModal v-model:show="showModal" preset="card" :title="isEdit ? '编辑赛事' : '新增赛事'" style="width:650px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NGrid :cols="2" :x-gap="16">
          <NGridItem :span="2"><NFormItem label="赛事名称"><NInput v-model:value="form.name" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="开始日期"><NInput v-model:value="form.startDate" placeholder="2025-05-01" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="结束日期"><NInput v-model:value="form.endDate" placeholder="2025-05-03" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="地点"><NInput v-model:value="form.location" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="状态"><NSelect v-model:value="form.status" :options="[{label:'筹备中',value:0},{label:'报名中',value:1},{label:'进行中',value:2},{label:'已结束',value:3}]" /></NFormItem></NGridItem>
          <NGridItem :span="2"><NFormItem label="描述"><NInput type="textarea" v-model:value="form.description" :rows="3" /></NFormItem></NGridItem>
        </NGrid>
      </NForm>
      <template #action><NButton type="primary" @click="handleSave">保存</NButton></template>
    </NModal>
  </div>
</template>
