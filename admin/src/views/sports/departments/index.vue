<script setup lang="ts">
import { ref, reactive, onMounted, h } from 'vue';
import { NButton, NSpace, NPopconfirm, NInput, NDataTable, NModal, NForm, NFormItem, NGrid, NGridItem, NInputNumber, NSelect, NTag, useMessage } from 'naive-ui';
import { fetchDepartmentList, createDepartment, updateDepartment, deleteDepartment } from '@/service/api/management';
const message = useMessage();
const loading = ref(false);
const data = ref<any[]>([]);
const showModal = ref(false);
const isEdit = ref(false);
const form = reactive<any>({ name: '', code: '', contactPerson: '', contactPhone: '', sortOrder: 1, status: 1 });
const columns: any[] = [
  { title: 'ID', key: 'id', width: 60 },
  { title: '院系名称', key: 'name' },
  { title: '院系代码', key: 'code', width: 100 },
  { title: '联系人', key: 'contactPerson', width: 100 },
  { title: '联系电话', key: 'contactPhone', width: 130 },
  { title: '排序', key: 'sortOrder', width: 80 },
  { title: '状态', key: 'status', width: 80, render: (r: any) => h(NTag, { type: r.status === 1 ? 'success' : 'error', size: 'small' }, () => r.status === 1 ? '启用' : '停用') },
  { title: '操作', key: 'actions', width: 150, render: (r: any) => h(NSpace, null, () => [
    h(NButton, { size: 'small', onClick: () => handleEdit(r) }, () => '编辑'),
    h(NPopconfirm, { onPositiveClick: () => handleDelete(r.id) }, { trigger: () => h(NButton, { size: 'small', type: 'error' }, () => '删除'), default: () => '确定删除？' })
  ]) }
];
async function loadData() {
  loading.value = true;
  try {
    const res = await fetchDepartmentList();
    const list = res.data as any;
    if (!res.error) {
      data.value = Array.isArray(list) ? list : list?.records || [];
    }
  } catch (e) {
  } finally {
    loading.value = false;
  }
}
function handleAdd() { isEdit.value = false; Object.assign(form, { id: null, name: '', code: '', contactPerson: '', contactPhone: '', sortOrder: 1, status: 1 }); showModal.value = true; }
function handleEdit(row: any) { isEdit.value = true; Object.assign(form, { ...row }); showModal.value = true; }
async function handleSave() { try { if (isEdit.value) { await updateDepartment(form.id, form); } else { await createDepartment(form); } message.success('保存成功'); showModal.value = false; loadData(); } catch(e) {} }
async function handleDelete(id: number) { try { await deleteDepartment(id); message.success('删除成功'); loadData(); } catch(e) {} }
onMounted(loadData);
</script>
<template>
  <div class="p-16px">
    <NSpace class="mb-16px" justify="end"><NButton type="primary" @click="handleAdd">新增院系</NButton></NSpace>
    <NDataTable :columns="columns" :data="data" :loading="loading" :bordered="false" />
    <NModal v-model:show="showModal" preset="card" :title="isEdit ? '编辑院系' : '新增院系'" style="width:500px">
      <NForm :model="form" label-placement="left" label-width="80px">
        <NGrid :cols="2" :x-gap="16">
          <NGridItem><NFormItem label="院系名称"><NInput v-model:value="form.name" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="院系代码"><NInput v-model:value="form.code" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="联系人"><NInput v-model:value="form.contactPerson" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="联系电话"><NInput v-model:value="form.contactPhone" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="排序"><NInputNumber v-model:value="form.sortOrder" :min="1" /></NFormItem></NGridItem>
          <NGridItem><NFormItem label="状态"><NSelect v-model:value="form.status" :options="[{ label: '启用', value: 1 }, { label: '停用', value: 0 }]" /></NFormItem></NGridItem>
        </NGrid>
      </NForm>
      <template #action><NButton type="primary" @click="handleSave">保存</NButton></template>
    </NModal>
  </div>
</template>
