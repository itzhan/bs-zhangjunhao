<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NTag, NPopconfirm, NInput, NSelect, NDataTable, NModal, NForm, NFormItem, NGrid, NGridItem, NInputNumber, useMessage } from 'naive-ui';
import { fetchSportList, createSport, updateSport, deleteSport, fetchEventList } from '@/service/api/management';
const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const total = ref(0);
const eventOptions = ref<any[]>([]);
const query = reactive({ page: 1, size: 10, eventId: null as any, keyword: '', category: '' });
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive<any>({ name: '', eventId: null, category: '田赛', genderLimit: 0, maxParticipants: null, unit: '', sortType: 0, description: '', status: 1 });
const genderLimitOpts = [{ label: '不限', value: 0 }, { label: '仅男', value: 1 }, { label: '仅女', value: 2 }];
const sortTypeOpts = [{ label: '升序(时间类)', value: 0 }, { label: '降序(距离类)', value: 1 }];
const columns: any[] = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '项目名称', key: 'name' },
  { title: '赛事', key: 'eventName' },
  { title: '分类', key: 'category' },
  { title: '性别限制', key: 'genderLimit', render: (r: any) => r.genderLimit === 0 ? '不限' : r.genderLimit === 1 ? '仅男' : '仅女' },
  { title: '最大人数', key: 'maxParticipants' },
  { title: '单位', key: 'unit' },
  { title: '状态', key: 'status', render: (r: any) => h(NTag, { type: r.status === 1 ? 'success' : 'error', size: 'small' }, () => r.status === 1 ? '正常' : '禁用') },
  { title: '操作', key: 'actions', width: 150, render: (r: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', onClick: () => handleEdit(r) }, () => '编辑'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(r.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确定删除？' })
  ]) }
];
async function loadData() {
  loading.value = true;
  try {
    const res = await fetchSportList(query);
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
function handleAdd() { isEdit.value = false; Object.assign(form, { id: null, name: '', eventId: null, category: '田赛', genderLimit: 0, maxParticipants: null, unit: '', sortType: 0, description: '', status: 1 }); showModal.value = true; }
function handleEdit(row: any) { isEdit.value = true; Object.assign(form, { ...row }); showModal.value = true; }
async function handleSave() { try { if (isEdit.value) { await updateSport(form.id, form); } else { await createSport(form); } message.success('保存成功'); showModal.value = false; loadData(); } catch(e) {} }
async function handleDelete(id: number) { try { await deleteSport(id); message.success('删除成功'); loadData(); } catch(e) {} }
function handlePageChange(p: number) { query.page = p; loadData(); }
onMounted(() => { loadEvents(); loadData(); });
</script>
<template>
  <div class="p-16px">
    <NSpace class="mb-16px" justify="space-between">
      <NSpace><NSelect v-model:value="query.eventId" :options="eventOptions" placeholder="筛选赛事" clearable style="width:200px" @update:value="loadData" />
        <NInput v-model:value="query.keyword" placeholder="搜索项目名称" clearable @keyup.enter="loadData" style="width:200px" /><NButton type="primary" @click="loadData">搜索</NButton></NSpace>
      <NButton type="primary" @click="handleAdd">新增项目</NButton>
    </NSpace>
    <NDataTable :columns="columns" :data="data" :loading="loading" :bordered="false" :pagination="{ page: query.page, pageSize: query.size, itemCount: total, onUpdatePage: handlePageChange }" remote />
    <NModal v-model:show="showModal" preset="card" :title="isEdit ? '编辑项目' : '新增项目'" style="width:700px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NGrid :cols="2" :x-gap="16">
          <NGridItem><NFormItem label="项目名称"><NInput v-model:value="form.name" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="所属赛事"><NSelect v-model:value="form.eventId" :options="eventOptions" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="分类"><NSelect v-model:value="form.category" :options="[{label:'田赛',value:'田赛'},{label:'径赛',value:'径赛'},{label:'趣味赛',value:'趣味赛'}]" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="性别限制"><NSelect v-model:value="form.genderLimit" :options="genderLimitOpts" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="最大人数"><NInputNumber v-model:value="form.maxParticipants" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="单位"><NInput v-model:value="form.unit" placeholder="秒/米/分" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="排序方式"><NSelect v-model:value="form.sortType" :options="sortTypeOpts" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="状态"><NSelect v-model:value="form.status" :options="[{label:'正常',value:1},{label:'禁用',value:0}]" /></NFormItem></NGridItem>
          <NGridItem :span="2"><NFormItem label="描述"><NInput type="textarea" v-model:value="form.description" :rows="2" /></NFormItem></NGridItem>
        </NGrid>
      </NForm>
      <template #action><NButton type="primary" @click="handleSave">保存</NButton></template>
    </NModal>
  </div>
</template>
