<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NPopconfirm, NInput, NSelect, NDataTable, NModal, NForm, NFormItem, NGrid, NGridItem, useMessage } from 'naive-ui';
import { fetchAnnouncementList, createAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/service/api/management';
const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const query = reactive({ page: 1, size: 10, type: null as any });
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive<any>({ title: '', content: '', type: 0, status: 1, isTop: 0 });
const typeMap: any = { 0: '通知', 1: '公告', 2: '规则' };
const queryTypeOptions: any[] = [{ label: '全部', value: null }, { label: '通知', value: 0 }, { label: '公告', value: 1 }, { label: '规则', value: 2 }];
const typeOptions: any[] = [{ label: '通知', value: 0 }, { label: '公告', value: 1 }, { label: '规则', value: 2 }];
const statusOptions: any[] = [{ label: '发布', value: 1 }, { label: '草稿', value: 0 }];
const topOptions: any[] = [{ label: '否', value: 0 }, { label: '是', value: 1 }];
const columns: any[] = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '标题', key: 'title', ellipsis: { tooltip: true } },
  { title: '类型', key: 'type', width: 80, render: (r: any) => typeMap[r.type] },
  { title: '置顶', key: 'isTop', width: 80, render: (r: any) => r.isTop ? h(NTag, { type: 'warning', size: 'small' }, () => '置顶') : '' },
  { title: '状态', key: 'status', width: 80, render: (r: any) => h(NTag, { type: r.status === 1 ? 'success' : 'error', size: 'small' }, () => r.status === 1 ? '发布' : '草稿') },
  { title: '浏览', key: 'viewCount', width: 70 },
  { title: '发布时间', key: 'publishTime', width: 120, render: (r: any) => r.publishTime?.substring(0, 10) },
  { title: '操作', key: 'actions', width: 150, render: (r: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', onClick: () => handleEdit(r) }, () => '编辑'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(r.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确定删除？' })
  ]) }
];
async function loadData() {
  loading.value = true;
  try {
    const res = await fetchAnnouncementList(query);
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
function handleAdd() { isEdit.value = false; Object.assign(form, { id: null, title: '', content: '', type: 0, status: 1, isTop: 0 }); showModal.value = true; }
function handleEdit(row: any) { isEdit.value = true; Object.assign(form, { ...row }); showModal.value = true; }
async function handleSave() { try { if (isEdit.value) { await updateAnnouncement(form.id, form); } else { await createAnnouncement(form); } message.success('保存成功'); showModal.value = false; loadData(); } catch(e) {} }
async function handleDelete(id: number) { try { await deleteAnnouncement(id); message.success('删除成功'); loadData(); } catch(e) {} }
function handlePageChange(p: number) { query.page = p; loadData(); }
onMounted(loadData);
</script>
<template>
  <div class="p-16px">
    <NSpace class="mb-16px" justify="space-between">
      <NSpace><NSelect v-model:value="query.type" :options="queryTypeOptions" style="width:120px" @update:value="loadData" /><NButton type="primary" @click="loadData">搜索</NButton></NSpace>
      <NButton type="primary" @click="handleAdd">发布公告</NButton>
    </NSpace>
    <NDataTable :columns="columns" :data="data" :loading="loading" :bordered="false" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" remote />
    <NModal v-model:show="showModal" preset="card" :title="isEdit ? '编辑公告' : '发布公告'" style="width:700px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NFormItem label="标题"><NInput v-model:value="form.title" /></NFormItem>
        <NGrid :cols="3" :x-gap="16">
          <NGridItem><NFormItem label="类型"><NSelect v-model:value="form.type" :options="typeOptions" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="状态"><NSelect v-model:value="form.status" :options="statusOptions" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="置顶"><NSelect v-model:value="form.isTop" :options="topOptions" /></NFormItem></NGridItem>
        </NGrid>
        <NFormItem label="内容"><NInput type="textarea" v-model:value="form.content" :rows="8" /></NFormItem>
      </NForm>
      <template #action><NButton type="primary" @click="handleSave">保存</NButton></template>
    </NModal>
  </div>
</template>
